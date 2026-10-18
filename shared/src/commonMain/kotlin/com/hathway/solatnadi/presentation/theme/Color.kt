package com.hathway.solatnadi.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * SolatNadi brand palette.
 *
 * Keep these raw palette values in one place. Prefer SolatNadiTheme.colorScheme
 * in UI code so components can adapt to light and dark themes.
 */
object SolatNadiPalette {
    // Brand greens
    val ForestGreen = Color(0xFF075747)
    val DeepGreen = Color(0xFF064638)
    val EmeraldGreen = Color(0xFF0B755B)
    val SoftGreen = Color(0xFF2E8B70)

    // Surfaces
    val White = Color(0xFFFFFFFF)
    val WarmWhite = Color(0xFFFFFEFC)
    val SoftMint = Color(0xFFF5FBF7)
    val ActiveMint = Color(0xFFDFF5E5)
    val ActiveMintStrong = Color(0xFFC8EED5)
    val Border = Color(0xFFE7EDE9)
    val Divider = Color(0xFFE9EFEB)

    // Text
    val TextPrimary = Color(0xFF163A38)
    val TextSecondary = Color(0xFF687782)
    val TextTertiary = Color(0xFF89949C)
    val TextOnPrimary = Color(0xFFFFFFFF)

    // Prayer and state accents
    val SunriseGold = Color(0xFFFFB000)
    val SunsetOrange = Color(0xFFF26A3D)
    val NightBlue = Color(0xFF31547A)
    val ToggleOff = Color(0xFFC9CDD2)
    val Success = Color(0xFF16845B)
    val Warning = Color(0xFFB66A12)
    val Error = Color(0xFFBA3737)
    val Info = Color(0xFF286A9B)

    // Decorative backgrounds
    val SkyBlue = Color(0xFFDCECF5)
    val HorizonPeach = Color(0xFFFFF0D7)
    val RingTrack = Color(0xFFC7EBDD)
}

/**
 * Compatibility aliases for screens that still use the earlier color names.
 * Prefer SolatNadiPalette for new code.
 */
val ForestGreen = SolatNadiPalette.ForestGreen
val PrimaryGreen = SolatNadiPalette.EmeraldGreen
val TextPrimary = SolatNadiPalette.TextPrimary
val TextSecondary = SolatNadiPalette.TextSecondary
