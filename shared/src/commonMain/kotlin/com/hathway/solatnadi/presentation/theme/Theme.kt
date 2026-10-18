package com.hathway.solatnadi.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

/**
 * Data container for non-Material semantic brand tokens.
 */
@Immutable
data class SolatNadiCustomColors(
    val sunriseGold: Color,
    val sunsetOrange: Color,
    val nightBlue: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
    val ringTrack: Color
)

/**
 * Specialized text configurations that fall outside standard Material 3 specs.
 */
@Immutable
data class SolatNadiCustomTypography(
    val countdown: androidx.compose.ui.text.TextStyle
)

private val LightCustomColors = SolatNadiCustomColors(
    sunriseGold = SolatNadiPalette.SunriseGold,
    sunsetOrange = SolatNadiPalette.SunsetOrange,
    nightBlue = SolatNadiPalette.NightBlue,
    success = SolatNadiPalette.Success,
    warning = SolatNadiPalette.Warning,
    error = SolatNadiPalette.Error,
    info = SolatNadiPalette.Info,
    ringTrack = SolatNadiPalette.RingTrack
)

private val DarkCustomColors = SolatNadiCustomColors(
    sunriseGold = Color(0xFFFFD078),
    sunsetOrange = Color(0xFFFFB39A),
    nightBlue = Color(0xFF8BB2DC),
    success = Color(0xFF76D7A8),
    warning = Color(0xFFFFD9A3),
    error = Color(0xFFFFB4AB),
    info = Color(0xFF96C9F4),
    ringTrack = Color(0xFF1B3D32) // Dark-optimized subtle track
)

private val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }
private val LocalCustomTypography = staticCompositionLocalOf { SolatNadiCustomTypography(countdown = CountdownTextStyle) }

private val SolatNadiLightColorScheme = lightColorScheme(
    primary = SolatNadiPalette.ForestGreen,
    onPrimary = SolatNadiPalette.TextOnPrimary,
    primaryContainer = SolatNadiPalette.ActiveMint,
    onPrimaryContainer = SolatNadiPalette.TextPrimary,

    secondary = SolatNadiPalette.EmeraldGreen,
    onSecondary = SolatNadiPalette.TextOnPrimary,
    secondaryContainer = SolatNadiPalette.SoftMint,
    onSecondaryContainer = SolatNadiPalette.TextPrimary,

    tertiary = SolatNadiPalette.SunriseGold,
    onTertiary = SolatNadiPalette.TextPrimary,

    background = SolatNadiPalette.White,
    onBackground = SolatNadiPalette.TextPrimary,

    surface = SolatNadiPalette.White,
    onSurface = SolatNadiPalette.TextPrimary,
    surfaceVariant = SolatNadiPalette.SoftMint,
    onSurfaceVariant = SolatNadiPalette.TextSecondary,

    outline = SolatNadiPalette.Border,
    outlineVariant = SolatNadiPalette.Divider,

    error = SolatNadiPalette.Error,
    onError = SolatNadiPalette.TextOnPrimary
)

private val SolatNadiDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DD5B7),
    onPrimary = Color(0xFF00382C),
    primaryContainer = Color(0xFF145B48),
    onPrimaryContainer = Color(0xFFD0F4E1),

    secondary = Color(0xFF9BDCC5),
    onSecondary = Color(0xFF073A2F),
    secondaryContainer = Color(0xFF204F42),
    onSecondaryContainer = Color(0xFFD6F5E6),

    tertiary = Color(0xFFFFD078),
    onTertiary = Color(0xFF3F2D00),

    background = Color(0xFF101916),
    onBackground = Color(0xFFE5EEE9),

    surface = Color(0xFF17211D),
    onSurface = Color(0xFFE5EEE9),
    surfaceVariant = Color(0xFF22332C),
    onSurfaceVariant = Color(0xFFB8C8C0),

    outline = Color(0xFF52645B),
    outlineVariant = Color(0xFF34473E),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

enum class SolatNadiThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Universal access token registry for the entire application layout system.
 * Fixed to correctly reference structural design tokens in Compose Multiplatform.
 */
object SolatNadiTheme {
    val customColors: SolatNadiCustomColors
        @Composable @ReadOnlyComposable get() = LocalCustomColors.current

    val customTypography: SolatNadiCustomTypography
        @Composable @ReadOnlyComposable get() = LocalCustomTypography.current

    val spacing: SolatNadiSpacing
        get() = SolatNadiSpacing

    val dimens: SolatNadiDimens
        get() = SolatNadiDimens

    val shapes: SolatNadiShapes
        get() = SolatNadiShapes
}

@Composable
fun SolatNadiTheme(
    themeMode: SolatNadiThemeMode = SolatNadiThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        SolatNadiThemeMode.SYSTEM -> isSystemInDarkTheme()
        SolatNadiThemeMode.LIGHT -> false
        SolatNadiThemeMode.DARK -> true
    }

    val selectedCustomColors = if (useDarkTheme) DarkCustomColors else LightCustomColors
    val customTypographyInstance = SolatNadiCustomTypography(countdown = CountdownTextStyle)

    CompositionLocalProvider(
        LocalCustomColors provides selectedCustomColors,
        LocalCustomTypography provides customTypographyInstance
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) {
                SolatNadiDarkColorScheme
            } else {
                SolatNadiLightColorScheme
            },
            typography = SolatNadiTypography,
            shapes = SolatNadiMaterialShapes,
            content = content
        )
    }
}
