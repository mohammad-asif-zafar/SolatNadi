package com.hathway.solatnadi.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object SolatNadiShapes {
    val ExtraSmall = RoundedCornerShape(8.dp)
    val Small = RoundedCornerShape(12.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(24.dp)
    val ExtraLarge = RoundedCornerShape(32.dp)
    val Pill = RoundedCornerShape(percent = 50)
}

val SolatNadiMaterialShapes = Shapes(
    extraSmall = SolatNadiShapes.ExtraSmall,
    small = SolatNadiShapes.Small,
    medium = SolatNadiShapes.Medium,
    large = SolatNadiShapes.Large,
    extraLarge = SolatNadiShapes.ExtraLarge
)
