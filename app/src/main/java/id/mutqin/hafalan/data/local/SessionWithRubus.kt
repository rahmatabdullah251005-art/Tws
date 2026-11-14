package id.mutqin.hafalan.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RubuWithSurahs(
    @Embedded val rubu: RubuStatusEntity,
    @Relation(
        parentColumn = "startSurahNo",
        entityColumn = "number"
    )
    val startSurah: SurahEntity,
    @Relation(
        parentColumn = "endSurahNo",
        entityColumn = "number"
    )
    val endSurah: SurahEntity
)

data class SessionWithRubus(
    @Embedded val session: HafalanSessionEntity,
    @Relation(
        entity = RubuStatusEntity::class,
        parentColumn = "sessionNo",
        entityColumn = "sessionNo"
    )
    val rubus: List<RubuWithSurahs>
) {
    val orderedRubus: List<RubuWithSurahs>
        get() = rubus.sortedBy { it.rubu.positionInSession }
}
