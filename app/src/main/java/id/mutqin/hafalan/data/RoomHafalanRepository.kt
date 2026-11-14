package id.mutqin.hafalan.data

import id.mutqin.hafalan.data.local.HafalanSessionEntity
import id.mutqin.hafalan.data.local.MutqinDao
import id.mutqin.hafalan.data.local.RubuStatusEntity
import id.mutqin.hafalan.data.local.RubuWithSurahs
import id.mutqin.hafalan.data.local.SessionWithRubus
import id.mutqin.hafalan.data.local.StudentProfileEntity
import id.mutqin.hafalan.data.local.SurahEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class RoomHafalanRepository(
    private val dao: MutqinDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HafalanRepository {

    override val studentName: Flow<String> = dao.observeStudentProfile()
        .map { it?.name.orEmpty() }
        .flowOn(ioDispatcher)

    override val sessions: Flow<List<HafalanSessionProgress>> = dao.observeSessionsWithRubus()
        .map { list -> list.map { it.toDomain() } }
        .flowOn(ioDispatcher)

    override val summary: Flow<HafalanSummary> = sessions
        .map { list ->
            val totalSessions = if (list.isNotEmpty()) list.size else 120
            val completedSessions = list.count { it.isSubmitted }
            val totalRubus = list.sumOf { it.rubus.size }.let { if (it > 0) it else 240 }
            val completedRubus = list.sumOf { session -> session.rubus.count { it.isCompleted } }
            val percent = if (totalSessions > 0) {
                ((completedSessions.toFloat() / totalSessions) * 100f).roundToInt()
            } else {
                0
            }
            HafalanSummary(
                completedSessions = completedSessions,
                totalSessions = totalSessions,
                completedRubus = completedRubus,
                totalRubus = totalRubus,
                progressPercent = percent
            )
        }
        .flowOn(ioDispatcher)

    override fun observeSessions(filter: JuzFilter): Flow<List<HafalanSessionProgress>> {
        return sessions.map { list -> list.filter { filter.matches(it.juz) } }
    }

    override fun observeSurahs(): Flow<List<SurahInfo>> {
        return dao.observeAllSurahs()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun initializeCurriculum() = withContext(ioDispatcher) {
        val sessionEntities = SessionsData.allSessions.map { session ->
            HafalanSessionEntity(
                sessionNo = session.no,
                juz = session.juz,
                setoranTitle = session.setoran
            )
        }
        val rubuEntities = SessionsData.allSessions.flatMap { session ->
            val rubu1Number = extractRubuNumber(session.rubu1, (session.no - 1) * 2 + 1)
            val rubu2Number = extractRubuNumber(session.rubu2, (session.no - 1) * 2 + 2)
            val (startSurah1, startAyah1) = parseVerseRef(session.awal1)
            val (endSurah1, endAyah1) = parseVerseRef(session.akhir1)
            val (startSurah2, startAyah2) = parseVerseRef(session.awal2)
            val (endSurah2, endAyah2) = parseVerseRef(session.akhir2)

            listOf(
                RubuStatusEntity(
                    rubuNumber = rubu1Number,
                    sessionNo = session.no,
                    positionInSession = 1,
                    rubuLabel = session.rubu1,
                    startSurahNo = startSurah1,
                    startAyah = startAyah1,
                    endSurahNo = endSurah1,
                    endAyah = endAyah1
                ),
                RubuStatusEntity(
                    rubuNumber = rubu2Number,
                    sessionNo = session.no,
                    positionInSession = 2,
                    rubuLabel = session.rubu2,
                    startSurahNo = startSurah2,
                    startAyah = startAyah2,
                    endSurahNo = endSurah2,
                    endAyah = endAyah2
                )
            )
        }
        dao.seedCurriculumIfNeeded(
            surahs = SurahData.allSurahs,
            sessions = sessionEntities,
            rubus = rubuEntities
        )
    }

    override suspend fun updateStudentName(name: String) = withContext(ioDispatcher) {
        dao.upsertStudentProfile(StudentProfileEntity(id = 1, name = name))
    }

    override suspend fun saveSessionSubmission(
        sessionNo: Int,
        date: String,
        grade: String,
        notes: String
    ) = withContext(ioDispatcher) {
        val trimmedDate = date.trim()
        val trimmedGrade = grade.trim()
        val isCleared = trimmedDate.isBlank() && trimmedGrade.isBlank() && notes.isBlank()

        dao.saveSessionAndSyncRubus(
            sessionNo = sessionNo,
            date = trimmedDate,
            grade = trimmedGrade,
            notes = notes,
            updatedAtMillis = if (isCleared) 0L else System.currentTimeMillis(),
            syncRubuCompletion = when {
                trimmedGrade.isNotBlank() -> true
                isCleared -> false
                else -> null
            }
        )
    }

    override suspend fun clearSessionSubmission(sessionNo: Int) = withContext(ioDispatcher) {
        dao.saveSessionAndSyncRubus(
            sessionNo = sessionNo,
            date = "",
            grade = "",
            notes = "",
            updatedAtMillis = 0L,
            syncRubuCompletion = false
        )
    }

    override suspend fun setRubuCompleted(rubuNumber: Int, isCompleted: Boolean) =
        withContext(ioDispatcher) {
            dao.updateRubuCompletion(
                rubuNumber = rubuNumber,
                isCompleted = isCompleted,
                completedAtMillis = if (isCompleted) System.currentTimeMillis() else null
            )
        }

    private fun extractRubuNumber(label: String, fallback: Int): Int {
        return label.substringAfterLast(' ').toIntOrNull() ?: fallback
    }

    private fun parseVerseRef(ref: String): Pair<Int, Int> {
        val parts = ref.split(':')
        val surah = parts.getOrNull(0)?.toIntOrNull() ?: 1
        val ayah = parts.getOrNull(1)?.toIntOrNull() ?: 1
        return surah to ayah
    }
}

private fun SurahEntity.toDomain(): SurahInfo = SurahInfo(
    number = number,
    nameIndonesian = nameIndonesian,
    nameArabic = nameArabic,
    ayahCount = ayahCount,
    revelationPlace = revelationPlace
)

private fun RubuWithSurahs.toDomain(): RubuProgress = RubuProgress(
    rubuNumber = rubu.rubuNumber,
    sessionNo = rubu.sessionNo,
    positionInSession = rubu.positionInSession,
    label = rubu.rubuLabel,
    startSurah = startSurah.toDomain(),
    startAyah = rubu.startAyah,
    endSurah = endSurah.toDomain(),
    endAyah = rubu.endAyah,
    isCompleted = rubu.isCompleted,
    completedAtMillis = rubu.completedAtMillis
)

private fun SessionWithRubus.toDomain(): HafalanSessionProgress = HafalanSessionProgress(
    sessionNo = session.sessionNo,
    juz = session.juz,
    setoranTitle = session.setoranTitle,
    submissionDate = session.submissionDate,
    grade = session.grade,
    notes = session.notes,
    updatedAtMillis = session.updatedAtMillis,
    rubus = orderedRubus.map { it.toDomain() }
)
