package id.mutqin.hafalan.data

import kotlinx.coroutines.flow.Flow

interface HafalanRepository {
    val studentName: Flow<String>
    val sessions: Flow<List<HafalanSessionProgress>>
    val summary: Flow<HafalanSummary>

    fun observeSessions(filter: JuzFilter): Flow<List<HafalanSessionProgress>>
    fun observeSurahs(): Flow<List<SurahInfo>>

    suspend fun initializeCurriculum()
    suspend fun updateStudentName(name: String)
    suspend fun saveSessionSubmission(
        sessionNo: Int,
        date: String,
        grade: String,
        notes: String
    )
    suspend fun clearSessionSubmission(sessionNo: Int)
    suspend fun setRubuCompleted(rubuNumber: Int, isCompleted: Boolean)
}
