package id.mutqin.hafalan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val nameIndonesian: String,
    val nameArabic: String,
    val ayahCount: Int,
    val revelationPlace: String
)
