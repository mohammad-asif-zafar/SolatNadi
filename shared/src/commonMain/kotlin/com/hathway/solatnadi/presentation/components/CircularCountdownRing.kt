package com.hathway.solatnadi.presentation.components

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.theme.*
import org.jetbrains.compose.resources.stringResource
import solatnadi.shared.generated.resources.Res
import solatnadi.shared.generated.resources.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularCountdownRing(
    modifier: Modifier = Modifier,
    countdownText: String,
    nextPrayerName: String,
    progress: Float = 0.65f,
    prayerName: PrayerName? = null
) {
    val themeSpacing = SolatNadiTheme.spacing
    val themeDimens = SolatNadiTheme.dimens
    val themeColors = SolatNadiTheme.customColors
    val themeTypography = SolatNadiTheme.customTypography

    val materialColorScheme = MaterialTheme.colorScheme
    val materialTypography = MaterialTheme.typography

    // Enforce solid data bounds
    val validatedProgress = progress.coerceIn(0f, 1f)

    val smoothProgress by animateFloatAsState(
        targetValue = validatedProgress,
        animationSpec = TweenSpec(durationMillis = 600),
        label = "PrayerCountdownProgressAnimation"
    )

    val sweepAngle = smoothProgress * 360f

    Box(
        modifier = modifier.size(themeDimens.CountdownDiameter),
        contentAlignment = Alignment.Center
    ) {
        // Core Ring Canvas Box Container
        Surface(
            modifier = Modifier
                .size(themeDimens.CountdownDiameter - 32.dp) // Proportional shrinkage
                .shadow(
                    elevation = themeDimens.CardElevation * 3,
                    shape = CircleShape,
                    spotColor = materialColorScheme.onBackground.copy(alpha = 0.1f)
                ),
            shape = CircleShape,
            color = materialColorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {

                // Optimized Path Rendering
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val strokeWidthPx = themeDimens.CountdownStroke.toPx()
                    val radius = (size.width - strokeWidthPx) / 2f
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    val arcTopLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
                    val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)

                    // 1. Draw the passive background ring track
                    drawArc(
                        color = themeColors.ringTrack,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx)
                    )

                    // 2. Draw the active progress line indicator
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = materialColorScheme.primary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(
                                width = strokeWidthPx,
                                cap = if (sweepAngle > 4f) StrokeCap.Round else StrokeCap.Butt
                            )
                        )

                        // 3. Optional Design Polish: Floating Tip Node Dot
                        val currentAngleRad = (sweepAngle - 90f) * (kotlin.math.PI / 180.0)
                        val dotCenter = Offset(
                            x = centerOffset.x + radius * cos(currentAngleRad).toFloat(),
                            y = centerOffset.y + radius * sin(currentAngleRad).toFloat()
                        )
                        drawCircle(
                            color = materialColorScheme.surface,
                            radius = strokeWidthPx * 0.4f,
                            center = dotCenter
                        )
                        drawCircle(
                            color = materialColorScheme.primary,
                            radius = strokeWidthPx * 0.25f,
                            center = dotCenter
                        )
                    }
                }

                // Inner Information Hierarchy Stack
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = themeSpacing.Large)
                ) {
                    Text(
                        text = stringResource(Res.string.next_prayer).uppercase(),
                        style = materialTypography.labelSmall.copy(letterSpacing = 1.5.sp),
                        color = materialColorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(themeSpacing.XSmall))

                    Text(
                        text = countdownText,
                        style = themeTypography.countdown,
                        color = materialColorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(Res.string.time_remaining),
                        style = materialTypography.bodySmall,
                        color = materialColorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(themeSpacing.Small))

                    Text(
                        text = nextPrayerName,
                        style = materialTypography.titleLarge,
                        color = materialColorScheme.onSurface
                    )
                }
            }
        }

        // Top Status Overlap Icon Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .size(44.dp)
                .shadow(themeDimens.CardElevation * 2, CircleShape)
                .clip(CircleShape)
                .background(themeColors.sunriseGold),
            contentAlignment = Alignment.Center
        ) {
            if (prayerName != null) {
                PrayerIcon(prayerName = prayerName, size = 24.dp)
            } else {
                DhuhrIcon(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Preview(name = "Light Theme", showBackground = true, backgroundColor = 0xFFF5FBF7)
@Composable
fun CountdownRingLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .padding(SolatNadiTheme.spacing.XXLarge), contentAlignment = Alignment.Center
        ) {
            CircularCountdownRing(
                countdownText = "00:42:12", nextPrayerName = "Asr", progress = 0.30f, prayerName = PrayerName.ASR
            )
        }
    }
}


@Preview(name = "Dark Theme", showBackground = true, backgroundColor = 0xFF17211D)
@Composable
fun CountdownRingDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .padding(SolatNadiTheme.spacing.XXLarge), contentAlignment = Alignment.Center
        ) {
            CircularCountdownRing(
                countdownText = "00:42:12", nextPrayerName = "Asr", progress = 0.30f, prayerName = PrayerName.ASR
            )
        }
    }
}
