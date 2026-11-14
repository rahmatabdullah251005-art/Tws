package id.mutqin.hafalan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hafalan_sessions")
data class HafalanSessionEntity(
    @PrimaryKey val sessionNo: Int,
    val juz: Int,
    val setoranTitle: String,
    val submissionDate: String = "",
    val grade: String = "",
    val notes: String = "",
    val updatedAtMillis: Long = 0L
)
