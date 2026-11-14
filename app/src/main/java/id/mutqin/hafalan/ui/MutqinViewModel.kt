package id.mutqin.hafalan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.mutqin.hafalan.data.HafalanRepository
import id.mutqin.hafalan.data.HafalanSessionProgress
import id.mutqin.hafalan.data.HafalanSummary
import id.mutqin.hafalan.data.JuzFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionDraft(
    val date: String,
    val grade: String,
    val notes: String,
    val showSavedMessage: Boolean = false
)

data class MutqinUiState(
    val studentName: String = "",
    val selectedFilter: JuzFilter = JuzFilter.ALL,
    val expandedSessions: Set<Int> = emptySet(),
    val filteredSessions: List<HafalanSessionProgress> = emptyList(),
    val drafts: Map<Int, SessionDraft> = emptyMap(),
    val summary: HafalanSummary = HafalanSummary()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MutqinViewModel(
    private val repository: HafalanRepository
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(JuzFilter.ALL)
    private val expandedSessions = MutableStateFlow<Set<Int>>(emptySet())
    private val drafts = MutableStateFlow<Map<Int, SessionDraft>>(emptyMap())
    private val localStudentName = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.initializeCurriculum()
        }
    }

    private val filteredSessionsFlow = selectedFilter.flatMapLatest { filter ->
        repository.observeSessions(filter)
    }

    val uiState: StateFlow<MutqinUiState> = combine(
        repository.studentName,
        localStudentName,
        filteredSessionsFlow,
        repository.summary,
        selectedFilter,
        expandedSessions,
        drafts
    ) { values ->
        val persistedName = values[0] as String
        val pendingName = values[1] as String?
        @Suppress("UNCHECKED_CAST")
        val filtered = values[2] as List<HafalanSessionProgress>
        val summary = values[3] as HafalanSummary
        val filter = values[4] as JuzFilter
        @Suppress("UNCHECKED_CAST")
        val expanded = values[5] as Set<Int>
        @Suppress("UNCHECKED_CAST")
        val draftMap = values[6] as Map<Int, SessionDraft>

        MutqinUiState(
            studentName = pendingName ?: persistedName,
            selectedFilter = filter,
            expandedSessions = expanded,
            filteredSessions = filtered,
            drafts = draftMap,
            summary = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MutqinUiState()
    )

    fun onStudentNameChange(name: String) {
        localStudentName.value = name
        viewModelScope.launch {
            repository.updateStudentName(name)
        }
    }

    fun onFilterSelected(filter: JuzFilter) {
        selectedFilter.value = filter
    }

    fun onToggleSession(sessionNo: Int) {
        expandedSessions.update { current ->
            if (sessionNo in current) current - sessionNo else current + sessionNo
        }
    }

    private fun currentDraftFor(sessionNo: Int): SessionDraft {
        val existing = drafts.value[sessionNo]
        if (existing != null) return existing
        val savedSession = uiState.value.filteredSessions
            .firstOrNull { it.sessionNo == sessionNo }
        return SessionDraft(
            date = savedSession?.submissionDate.orEmpty(),
            grade = savedSession?.grade.orEmpty(),
            notes = savedSession?.notes.orEmpty(),
            showSavedMessage = false
        )
    }

    fun onDateChanged(sessionNo: Int, date: String) {
        val current = currentDraftFor(sessionNo)
        drafts.update { map ->
            map + (sessionNo to current.copy(date = date, showSavedMessage = false))
        }
    }

    fun onGradeChanged(sessionNo: Int, grade: String) {
        val current = currentDraftFor(sessionNo)
        drafts.update { map ->
            map + (sessionNo to current.copy(grade = grade, showSavedMessage = false))
        }
    }

    fun onNotesChanged(sessionNo: Int, notes: String) {
        val current = currentDraftFor(sessionNo)
        drafts.update { map ->
            map + (sessionNo to current.copy(notes = notes, showSavedMessage = false))
        }
    }

    fun onToggleRubuStatus(rubuNumber: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setRubuCompleted(rubuNumber = rubuNumber, isCompleted = isCompleted)
        }
    }

    fun onSaveSession(sessionNo: Int) {
        val draft = currentDraftFor(sessionNo)
        viewModelScope.launch {
            repository.saveSessionSubmission(
                sessionNo = sessionNo,
                date = draft.date,
                grade = draft.grade,
                notes = draft.notes
            )
            drafts.update { map ->
                map + (sessionNo to draft.copy(showSavedMessage = true))
            }
        }
    }

    class Factory(private val repository: HafalanRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MutqinViewModel::class.java)) {
                return MutqinViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
