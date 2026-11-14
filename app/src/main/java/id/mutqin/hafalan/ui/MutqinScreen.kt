package id.mutqin.hafalan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.mutqin.hafalan.R
import id.mutqin.hafalan.ui.components.JuzFilterRow
import id.mutqin.hafalan.ui.components.MutqinHeader
import id.mutqin.hafalan.ui.components.SessionCard
import id.mutqin.hafalan.ui.components.StudentProgressPanel
import id.mutqin.hafalan.ui.theme.MutqinThemeTokens

@Composable
fun MutqinScreen(
    viewModel: MutqinViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = MutqinThemeTokens.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            MutqinHeader()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 720.dp),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "student_progress_panel") {
                        StudentProgressPanel(
                            studentName = uiState.studentName,
                            completedCount = uiState.summary.completedSessions,
                            totalCount = uiState.summary.totalSessions,
                            completedRubuCount = uiState.summary.completedRubus,
                            totalRubuCount = uiState.summary.totalRubus,
                            progressPercent = uiState.summary.progressPercent,
                            onStudentNameChange = viewModel::onStudentNameChange
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                    }

                    item(key = "juz_filter_row") {
                        JuzFilterRow(
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelected = viewModel::onFilterSelected
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    if (uiState.filteredSessions.isEmpty()) {
                        item(key = "empty_state") {
                            Text(
                                text = stringResource(R.string.empty_sessions_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }
                    } else {
                        items(
                            items = uiState.filteredSessions,
                            key = { it.sessionNo }
                        ) { session ->
                            SessionCard(
                                session = session,
                                draft = uiState.drafts[session.sessionNo],
                                isExpanded = session.sessionNo in uiState.expandedSessions,
                                onToggleExpand = { viewModel.onToggleSession(session.sessionNo) },
                                onToggleRubuStatus = viewModel::onToggleRubuStatus,
                                onDateChanged = { viewModel.onDateChanged(session.sessionNo, it) },
                                onGradeChanged = { viewModel.onGradeChanged(session.sessionNo, it) },
                                onNotesChanged = { viewModel.onNotesChanged(session.sessionNo, it) },
                                onSave = { viewModel.onSaveSession(session.sessionNo) }
                            )
                        }
                    }

                    item(key = "footer") {
                        Text(
                            text = stringResource(R.string.footer_device_storage),
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.footerText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        )
                    }
                }
            }
        }
    }
}
