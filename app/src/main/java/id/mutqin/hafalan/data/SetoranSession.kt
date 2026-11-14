package id.mutqin.hafalan.data

import androidx.annotation.StringRes
import id.mutqin.hafalan.R

data class SetoranSession(
    val no: Int,
    val juz: Int,
    val setoran: String,
    val rubu1: String,
    val awal1: String,
    val akhir1: String,
    val rubu2: String,
    val awal2: String,
    val akhir2: String
)

enum class JuzFilter(val id: Int, @param:StringRes val labelRes: Int) {
    ALL(0, R.string.filter_all_juz),
    JUZ_1_10(1, R.string.filter_juz_1_10),
    JUZ_11_20(2, R.string.filter_juz_11_20),
    JUZ_21_30(3, R.string.filter_juz_21_30);

    fun matches(juz: Int): Boolean = when (this) {
        ALL -> true
        JUZ_1_10 -> juz in 1..10
        JUZ_11_20 -> juz in 11..20
        JUZ_21_30 -> juz in 21..30
    }
}

enum class HafalanGrade(val label: String) {
    MUMTAZ("Mumtaz"),
    JAYYID_JIDDAN("Jayyid Jiddan"),
    JAYYID("Jayyid"),
    MAQBUL("Maqbul"),
    DHAIF("Dhaif")
}
