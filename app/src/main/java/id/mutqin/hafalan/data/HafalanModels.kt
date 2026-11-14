package id.mutqin.hafalan.data

data class SurahInfo(
    val number: Int,
    val nameIndonesian: String,
    val nameArabic: String,
    val ayahCount: Int,
    val revelationPlace: String
)

data class RubuProgress(
    val rubuNumber: Int,
    val sessionNo: Int,
    val positionInSession: Int,
    val label: String,
    val startSurah: SurahInfo,
    val startAyah: Int,
    val endSurah: SurahInfo,
    val endAyah: Int,
    val isCompleted: Boolean,
    val completedAtMillis: Long?
) {
    val startVerseRef: String
        get() = "${startSurah.number}:$startAyah"

    val endVerseRef: String
        get() = "${endSurah.number}:$endAyah"
}

data class HafalanSessionProgress(
    val sessionNo: Int,
    val juz: Int,
    val setoranTitle: String,
    val submissionDate: String,
    val grade: String,
    val notes: String,
    val updatedAtMillis: Long,
    val rubus: List<RubuProgress>
) {
    val isSubmitted: Boolean
        get() = grade.isNotBlank()
}

data class HafalanSummary(
    val completedSessions: Int = 0,
    val totalSessions: Int = 120,
    val completedRubus: Int = 0,
    val totalRubus: Int = 240,
    val progressPercent: Int = 0
)
