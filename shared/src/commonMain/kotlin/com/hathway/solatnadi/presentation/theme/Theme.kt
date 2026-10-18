package com.hathway.solatnadi.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

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
