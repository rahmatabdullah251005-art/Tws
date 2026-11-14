package id.mutqin.hafalan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import id.mutqin.hafalan.data.JuzFilter
import id.mutqin.hafalan.ui.theme.MutqinThemeTokens

@Composable
fun JuzFilterRow(
    selectedFilter: JuzFilter,
    onFilterSelected: (JuzFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = MutqinThemeTokens.colors

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(JuzFilter.entries, key = { it.id }) { filter ->
            val isSelected = filter == selectedFilter
            val pillShape = RoundedCornerShape(30.dp)
            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
            val borderColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                tokens.cardBorder
            }
            val textColor = if (isSelected) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Surface(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(pillShape)
                    .semantics {
                        selected = isSelected
                    }
                    .clickable(
                        role = Role.Tab,
                        onClick = { onFilterSelected(filter) }
                    )
                    .testTag("filter_chip_${filter.id}"),
                shape = pillShape,
                color = backgroundColor,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(filter.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
