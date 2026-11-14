package id.mutqin.hafalan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MutqinExtendedColors(
    val headerStart: Color,
    val headerEnd: Color,
    val headerSubtitle: Color,
    val goldAccent: Color,
    val goldSoft: Color,
    val progressStart: Color,
    val progressEnd: Color,
    val progressTrack: Color,
    val cardBorder: Color,
    val inputBorder: Color,
    val rubuSurface: Color,
    val doneTagBg: Color,
    val doneTagText: Color,
    val emptyTagBg: Color,
    val savedText: Color,
    val footerText: Color
)

private val LightExtendedColors = MutqinExtendedColors(
    headerStart = EmeraldPrimary,
    headerEnd = EmeraldDeep,
    headerSubtitle = HeaderSubtitleText,
    goldAccent = GoldAccent,
    goldSoft = GoldSoft,
    progressStart = EmeraldMid,
    progressEnd = GoldAccent,
    progressTrack = ProgressTrack,
    cardBorder = LineBorder,
    inputBorder = InputBorder,
    rubuSurface = RubuBackground,
    doneTagBg = DoneTagBg,
    doneTagText = DoneTagText,
    emptyTagBg = EmptyTagBg,
    savedText = SavedGreenText,
    footerText = FooterMutedText
)

private val DarkExtendedColors = MutqinExtendedColors(
    headerStart = EmeraldPrimary,
    headerEnd = EmeraldDeep,
    headerSubtitle = HeaderSubtitleText,
    goldAccent = GoldAccent,
    goldSoft = GoldSoft,
    progressStart = EmeraldMid,
    progressEnd = GoldAccent,
    progressTrack = DarkBorder,
    cardBorder = DarkBorder,
    inputBorder = DarkBorder,
    rubuSurface = DarkRubuSurface,
    doneTagBg = DarkDoneTagBg,
    doneTagText = DarkDoneTagText,
    emptyTagBg = DarkRubuSurface,
    savedText = DarkDoneTagText,
    footerText = DarkMutedText
)

val LocalMutqinColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = DoneTagBg,
    onPrimaryContainer = DoneTagText,
    secondary = GoldAccent,
    onSecondary = EmeraldDeep,
    background = CreamBackground,
    onBackground = InkText,
    surface = SurfaceWhite,
    onSurface = InkText,
    surfaceVariant = RubuBackground,
    onSurfaceVariant = MutedText,
    outline = InputBorder,
    outlineVariant = LineBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldMid,
    onPrimary = Color.White,
    primaryContainer = DarkDoneTagBg,
    onPrimaryContainer = DarkDoneTagText,
    secondary = GoldAccent,
    onSecondary = EmeraldDeep,
    background = DarkCreamBackground,
    onBackground = DarkInkText,
    surface = DarkSurfaceCard,
    onSurface = DarkInkText,
    surfaceVariant = DarkRubuSurface,
    onSurfaceVariant = DarkMutedText,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

@Composable
fun MutqinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalMutqinColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MutqinTypography,
            shapes = MutqinShapes,
            content = content
        )
    }
}

object MutqinThemeTokens {
    val colors: MutqinExtendedColors
        @Composable get() = LocalMutqinColors.current
}
