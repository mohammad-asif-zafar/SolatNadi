package com.hathway.solatnadi.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

@Composable
fun PrayerIcon(
    prayerName: PrayerName,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val iconModifier = modifier.size(size)
    when (prayerName) {
        PrayerName.FAJR -> FajrIcon(iconModifier)
        PrayerName.DHUHR -> DhuhrIcon(iconModifier)
        PrayerName.ASR -> AsrIcon(iconModifier)
        PrayerName.MAGHRIB -> MaghribIcon(iconModifier)
        PrayerName.ISHA -> IshaIcon(iconModifier)
    }
}

@Composable
fun FajrIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawArc(
            color = SolatNadiPalette.SunriseGold,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, h * 0.25f),
            size = Size(w * 0.5f, h * 0.5f)
        )

        drawLine(
            color = SolatNadiPalette.SunsetOrange,
            start = Offset(w * 0.1f, h * 0.72f),
            end = Offset(w * 0.9f, h * 0.72f),
            strokeWidth = w * 0.08f
        )
        drawLine(
            color = SolatNadiPalette.SunriseGold,
            start = Offset(w * 0.2f, h * 0.86f),
            end = Offset(w * 0.8f, h * 0.86f),
            strokeWidth = w * 0.06f
        )
    }
}

@Composable
fun DhuhrIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width * 0.24f

        drawCircle(
            color = SolatNadiPalette.SunriseGold,
            radius = radius,
            center = center
        )

        val rayLength = size.width * 0.10f
        val rayOffset = size.width * 0.32f
        val numRays = 8
        for (i in 0 until numRays) {
            val angle = i * (2 * kotlin.math.PI / numRays)
            val startX = center.x + (rayOffset * kotlin.math.cos(angle)).toFloat()
            val startY = center.y + (rayOffset * kotlin.math.sin(angle)).toFloat()
            val endX = center.x + ((rayOffset + rayLength) * kotlin.math.cos(angle)).toFloat()
            val endY = center.y + ((rayOffset + rayLength) * kotlin.math.sin(angle)).toFloat()

            drawLine(
                color = SolatNadiPalette.SunriseGold,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = size.width * 0.07f
            )
        }
    }
}

@Composable
fun AsrIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = SolatNadiPalette.SunriseGold,
            radius = w * 0.22f,
            center = Offset(w * 0.42f, h * 0.38f)
        )

        val cloudPath = Path().apply {
            moveTo(w * 0.25f, h * 0.72f)
            cubicTo(w * 0.10f, h * 0.72f, w * 0.10f, h * 0.52f, w * 0.28f, h * 0.52f)
            cubicTo(w * 0.32f, h * 0.38f, w * 0.58f, h * 0.38f, w * 0.62f, h * 0.52f)
            cubicTo(w * 0.80f, h * 0.72f, w * 0.80f, h * 0.72f, w * 0.68f, h * 0.72f)
            close()
        }
        drawPath(
            path = cloudPath,
            color = Color(0xFFD6E4EE)
        )
    }
}

@Composable
fun MaghribIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawArc(
            color = SolatNadiPalette.SunsetOrange,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, h * 0.28f),
            size = Size(w * 0.5f, h * 0.5f)
        )

        drawLine(
            color = SolatNadiPalette.SunsetOrange,
            start = Offset(w * 0.1f, h * 0.75f),
            end = Offset(w * 0.9f, h * 0.75f),
            strokeWidth = w * 0.08f
        )
    }
}

@Composable
fun IshaIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val outerPath = Path().apply {
            addOval(Rect(w * 0.20f, h * 0.20f, w * 0.80f, h * 0.80f))
        }
        val innerPath = Path().apply {
            addOval(Rect(w * 0.35f, h * 0.15f, w * 0.90f, h * 0.75f))
        }

        drawPath(
            path = Path.combine(
                operation = PathOperation.Difference,
                path1 = outerPath,
                path2 = innerPath
            ),
            color = SolatNadiPalette.NightBlue
        )

        drawCircle(
            color = SolatNadiPalette.NightBlue,
            radius = w * 0.035f,
            center = Offset(w * 0.75f, h * 0.35f)
        )
        drawCircle(
            color = SolatNadiPalette.NightBlue,
            radius = w * 0.025f,
            center = Offset(w * 0.65f, h * 0.25f)
        )
    }
}
