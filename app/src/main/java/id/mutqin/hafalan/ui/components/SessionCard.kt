package id.mutqin.hafalan.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.mutqin.hafalan.R
import id.mutqin.hafalan.data.HafalanGrade
import id.mutqin.hafalan.data.HafalanSessionProgress
import id.mutqin.hafalan.data.RubuProgress
import id.mutqin.hafalan.ui.SessionDraft
import id.mutqin.hafalan.ui.theme.MutqinThemeTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionCard(
    session: HafalanSessionProgress,
    draft: SessionDraft?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleRubuStatus: (rubuNumber: Int, isCompleted: Boolean) -> Unit,
    onDateChanged: (String) -> Unit,
    onGradeChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = MutqinThemeTokens.colors
    val rubu1Label = session.rubus.getOrNull(0)?.label.orEmpty()
    val rubu2Label = session.rubus.getOrNull(1)?.label.orEmpty()

    val currentDate = draft?.date ?: session.submissionDate
    val currentGrade = draft?.grade ?: session.grade
    val currentNotes = draft?.notes ?: session.notes
    val showSavedMessage = draft?.showSavedMessage == true

    val isDone = session.isSubmitted

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "chevron_rotation"
    )

    var showDatePicker by remember { mutableStateOf(false) }
    var gradeDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("session_card_${session.sessionNo}"),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, tokens.cardBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .testTag("session_header_${session.sessionNo}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            R.string.session_title_format,
                            session.juz,
                            session.setoranTitle
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = stringResource(
                            R.string.session_subtitle_format,
                            rubu1Label,
                            rubu2Label
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDone) tokens.doneTagBg else tokens.emptyTagBg)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("session_status_tag_${session.sessionNo}")
                    ) {
                        Text(
                            text = if (isDone) {
                                stringResource(R.string.status_done_format, session.grade)
                            } else {
                                stringResource(R.string.status_not_submitted)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDone) tokens.doneTagText else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.cd_expand_session),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(220)) + fadeIn(tween(180)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(150))
            ) {
                Column {
                    HorizontalDivider(thickness = 1.dp, color = tokens.cardBorder)
                    Column(modifier = Modifier.padding(14.dp)) {
                        session.rubus.forEachIndexed { index, rubu ->
                            RubuBlock(
                                rubu = rubu,
                                onToggleStatus = { checked ->
                                    onToggleRubuStatus(rubu.rubuNumber, checked)
                                }
                            )
                            if (index < session.rubus.lastIndex) {
                                Spacer(modifier = Modifier.height(7.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.label_submission_date),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true }
                                ) {
                                    OutlinedTextField(
                                        value = currentDate,
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("date_input_${session.sessionNo}"),
                                        placeholder = {
                                            Text(
                                                text = stringResource(R.string.placeholder_submission_date),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        trailingIcon = {
                                            if (currentDate.isNotEmpty()) {
                                                IconButton(
                                                    onClick = { onDateChanged("") },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Close,
                                                        contentDescription = stringResource(R.string.cd_clear_date),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Rounded.CalendarToday,
                                                    contentDescription = stringResource(R.string.cd_pick_date),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(11.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                            disabledBorderColor = tokens.inputBorder,
                                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            ),
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.label_hafalan_grade),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                ExposedDropdownMenuBox(
                                    expanded = gradeDropdownExpanded,
                                    onExpandedChange = { gradeDropdownExpanded = !gradeDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = currentGrade,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier
                                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                            .fillMaxWidth()
                                            .testTag("grade_dropdown_${session.sessionNo}"),
                                        placeholder = {
                                            Text(
                                                text = stringResource(R.string.placeholder_select_grade),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = gradeDropdownExpanded
                                            )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(11.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = tokens.inputBorder,
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = gradeDropdownExpanded,
                                        onDismissRequest = { gradeDropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = stringResource(R.string.placeholder_select_grade),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            onClick = {
                                                onGradeChanged("")
                                                gradeDropdownExpanded = false
                                            }
                                        )
                                        HafalanGrade.entries.forEach { grade ->
                                            DropdownMenuItem(
                                                text = { Text(text = grade.label) },
                                                onClick = {
                                                    onGradeChanged(grade.label)
                                                    gradeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.label_notes),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = currentNotes,
                            onValueChange = onNotesChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notes_input_${session.sessionNo}"),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.placeholder_notes),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(11.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = tokens.inputBorder,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onSave,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("save_button_${session.sessionNo}"),
                            shape = RoundedCornerShape(11.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.action_save_submission),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        if (showSavedMessage) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.feedback_saved_on_device),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = tokens.savedText,
                                modifier = Modifier.testTag("saved_feedback_${session.sessionNo}")
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = remember(currentDate) { parseIsoDateToUtcMillis(currentDate) }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            onDateChanged(formatUtcMillisToIsoDate(selectedMillis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_confirm_date))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun RubuBlock(
    rubu: RubuProgress,
    onToggleStatus: (Boolean) -> Unit
) {
    val tokens = MutqinThemeTokens.colors
    val toggleDescription = stringResource(R.string.cd_toggle_rubu_status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(tokens.rubuSurface)
            .clickable { onToggleStatus(!rubu.isCompleted) }
            .padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 4.dp)
            .testTag("rubu_block_${rubu.rubuNumber}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rubu.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(
                    R.string.rubu_range_format,
                    rubu.startSurah.nameIndonesian,
                    rubu.startVerseRef,
                    rubu.endSurah.nameIndonesian,
                    rubu.endVerseRef
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Checkbox(
            checked = rubu.isCompleted,
            onCheckedChange = onToggleStatus,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = toggleDescription }
                .testTag("rubu_checkbox_${rubu.rubuNumber}"),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun formatUtcMillisToIsoDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

private fun parseIsoDateToUtcMillis(dateString: String): Long? {
    if (dateString.isBlank()) return null
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(dateString)?.time
    } catch (_: Exception) {
        null
    }
}
