package id.mutqin.hafalan.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "rubu_statuses",
    foreignKeys = [
        ForeignKey(
            entity = HafalanSessionEntity::class,
            parentColumns = ["sessionNo"],
            childColumns = ["sessionNo"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SurahEntity::class,
            parentColumns = ["number"],
            childColumns = ["startSurahNo"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SurahEntity::class,
            parentColumns = ["number"],
            childColumns = ["endSurahNo"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class RubuStatusEntity(
    @PrimaryKey val rubuNumber: Int,
    @ColumnInfo(index = true) val sessionNo: Int,
    val positionInSession: Int,
    val rubuLabel: String,
    @ColumnInfo(index = true) val startSurahNo: Int,
    val startAyah: Int,
    @ColumnInfo(index = true) val endSurahNo: Int,
    val endAyah: Int,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null
) {
    val startVerseRef: String
        get() = "$startSurahNo:$startAyah"

    val endVerseRef: String
        get() = "$endSurahNo:$endAyah"
}
