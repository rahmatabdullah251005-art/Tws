package id.mutqin.hafalan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MutqinDao {

    @Transaction
    @Query("SELECT * FROM hafalan_sessions ORDER BY sessionNo ASC")
    fun observeSessionsWithRubus(): Flow<List<SessionWithRubus>>

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun observeAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun observeStudentProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT COUNT(*) FROM hafalan_sessions")
    suspend fun getSessionCount(): Int

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Query("SELECT COUNT(*) FROM rubu_statuses")
    suspend fun getRubuCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSessions(sessions: List<HafalanSessionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRubus(rubus: List<RubuStatusEntity>)

    @Transaction
    suspend fun seedCurriculumIfNeeded(
        surahs: List<SurahEntity>,
        sessions: List<HafalanSessionEntity>,
        rubus: List<RubuStatusEntity>
    ) {
        if (getSurahCount() == 0) {
            insertSurahs(surahs)
        }
        if (getSessionCount() == 0) {
            insertSessions(sessions)
        }
        if (getRubuCount() == 0) {
            insertRubus(rubus)
        }
    }

    @Query(
        """
        UPDATE hafalan_sessions
        SET submissionDate = :date,
            grade = :grade,
            notes = :notes,
            updatedAtMillis = :updatedAtMillis
        WHERE sessionNo = :sessionNo
        """
    )
    suspend fun updateSessionSubmission(
        sessionNo: Int,
        date: String,
        grade: String,
        notes: String,
        updatedAtMillis: Long
    )

    @Query(
        """
        UPDATE rubu_statuses
        SET isCompleted = :isCompleted,
            completedAtMillis = :completedAtMillis
        WHERE rubuNumber = :rubuNumber
        """
    )
    suspend fun updateRubuCompletion(
        rubuNumber: Int,
        isCompleted: Boolean,
        completedAtMillis: Long?
    )

    @Query(
        """
        UPDATE rubu_statuses
        SET isCompleted = :isCompleted,
            completedAtMillis = :completedAtMillis
        WHERE sessionNo = :sessionNo
        """
    )
    suspend fun updateSessionRubusCompletion(
        sessionNo: Int,
        isCompleted: Boolean,
        completedAtMillis: Long?
    )

    @Transaction
    suspend fun saveSessionAndSyncRubus(
        sessionNo: Int,
        date: String,
        grade: String,
        notes: String,
        updatedAtMillis: Long,
        syncRubuCompletion: Boolean?
    ) {
        updateSessionSubmission(
            sessionNo = sessionNo,
            date = date,
            grade = grade,
            notes = notes,
            updatedAtMillis = updatedAtMillis
        )
        if (syncRubuCompletion != null) {
            updateSessionRubusCompletion(
                sessionNo = sessionNo,
                isCompleted = syncRubuCompletion,
                completedAtMillis = if (syncRubuCompletion) updatedAtMillis else null
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudentProfile(profile: StudentProfileEntity)
}
