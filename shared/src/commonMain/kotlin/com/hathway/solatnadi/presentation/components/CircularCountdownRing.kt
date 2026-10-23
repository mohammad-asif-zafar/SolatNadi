package com.hathway.solatnadi.presentation.components

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun CircularCountdownRing(
    countdownText: String,
    nextPrayerName: String,
    progress: Float = 0.65f,
    prayerName: PrayerName? = null,
    modifier: Modifier = Modifier
) {
    val themeSpacing = SolatNadiTheme.spacing
    val themeDimens = SolatNadiTheme.dimens
    val themeColors = SolatNadiTheme.customColors
    val themeTypography = SolatNadiTheme.customTypography

    val materialColorScheme = MaterialTheme.colorScheme
    val materialTypography = MaterialTheme.typography

    val validatedProgress = progress.coerceIn(0f, 1f)

    val smoothProgress by animateFloatAsState(
        targetValue = validatedProgress,
        animationSpec = TweenSpec(durationMillis = 600),
        label = "PrayerCountdownProgressAnimation"
    )

    val sweepAngle = smoothProgress * 360f

    Box(
        modifier = modifier.size(themeDimens.CountdownDiameter), contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(themeDimens.CountdownDiameter - 40.dp).shadow(
                elevation = themeDimens.CardElevation * 4,
                shape = CircleShape,
                spotColor = materialColorScheme.onBackground.copy(alpha = 0.12f)
            ), shape = CircleShape, color = materialColorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val nativeStrokeWidth = themeDimens.CountdownStroke.toPx()
                    val coreDiameter = size.minDimension - nativeStrokeWidth
                    val targetOffset = Offset(nativeStrokeWidth / 2f, nativeStrokeWidth / 2f)
                    val internalArcBounds = Size(coreDiameter, coreDiameter)

                    drawArc(
                        color = themeColors.ringTrack,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = targetOffset,
                        size = internalArcBounds,
                        style = Stroke(width = nativeStrokeWidth)
                    )

                    if (sweepAngle > 0f) {
                        drawArc(
                            color = materialColorScheme.primary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = targetOffset,
                            size = internalArcBounds,
                            style = Stroke(
                                width = nativeStrokeWidth,
                                cap = if (sweepAngle > 6f) StrokeCap.Round else StrokeCap.Butt
                            )
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = themeSpacing.Large)
                ) {
                    Text(
                        text = stringResource(Res.string.next_prayer),
                        style = materialTypography.labelSmall,
                        color = materialColorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
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
                        color = materialColorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(themeSpacing.XSmall))

                    Text(
                        text = nextPrayerName,
                        style = materialTypography.headlineMedium,
                        color = materialColorScheme.primary
                    )
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-4).dp).size(44.dp)
                .shadow(themeDimens.CardElevation * 2, CircleShape).clip(CircleShape)
                .background(themeColors.sunriseGold), contentAlignment = Alignment.Center
        ) {
            if (prayerName != null) {
                PrayerIcon(prayerName = prayerName, size = 26.dp)
            } else {
                DhuhrIcon(modifier = Modifier.size(26.dp))
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
