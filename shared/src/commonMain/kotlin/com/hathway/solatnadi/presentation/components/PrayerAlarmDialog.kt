package com.hathway.solatnadi.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode

data class ActiveAlarmData(
    val prayerName: PrayerName = PrayerName.FAJR, val timeFormatted: String = "05:47 AM"
)

@Composable
fun PrayerAlarmDialog(
    alarmData: ActiveAlarmData,
    onStopAlarm: () -> Unit,
    onSnooze: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = false
        )
    ) {
        PrayerAlarmDialogContent(
            alarmData = alarmData,
            onStopAlarm = onStopAlarm,
            onSnooze = onSnooze,
            modifier = modifier
        )
    }
}

@Composable
fun PrayerAlarmDialogContent(
    alarmData: ActiveAlarmData,
    onStopAlarm: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    val spacing = SolatNadiTheme.spacing
    val shapes = SolatNadiTheme.shapes
    val dimens = SolatNadiTheme.dimens

    // Setup Infinite Transition Specs for the pulsing waves
    val infiniteTransition = rememberInfiniteTransition(label = "AlarmPulse")

    // Wave 1: Scales up and fades out
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f, targetValue = 1.8f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveScale1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0.0f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveAlpha1"
    )

    // Wave 2: Staggered delay (simulated via an offset/longer transition frame)
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f, targetValue = 1.5f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveScale2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.0f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "WaveAlpha2"
    )

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = spacing.Small),
        shape = shapes.Large,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.CardElevation)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.XXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Stack waves and core icon together inside a wrapper Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = spacing.Medium)
            ) {
                // Animated Pulse Ring Wave 1
                Box(
                    modifier = Modifier.size(dimens.CountdownIconSize).scale(pulseScale1)
                        .alpha(pulseAlpha1)
                        .background(colorScheme.primaryContainer, shape = CircleShape)
                )

                // Animated Pulse Ring Wave 2
                Box(
                    modifier = Modifier.size(dimens.CountdownIconSize).scale(pulseScale2)
                        .alpha(pulseAlpha2)
                        .background(colorScheme.primaryContainer, shape = CircleShape)
                )

                // Central Bell Badge Icon
                Surface(
                    modifier = Modifier.size(dimens.CountdownIconSize),
                    shape = CircleShape,
                    color = colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🔔", fontSize = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.XLarge))

            // Title: It's time for [PrayerName]
            Text(
                text = "It's time for ${alarmData.prayerName.displayName}",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.XSmall))

            // Time: 05:47 AM
            Text(
                text = alarmData.timeFormatted,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(spacing.XSmall))

            // Subtitle: It's time to prepare for prayer
            Text(
                text = "It's time to prepare for prayer",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.Huge))

            // Primary Button: ■ Stop Alarm
            Button(
                onClick = onStopAlarm,
                modifier = Modifier.fillMaxWidth().height(dimens.IconButtonSize),
                shape = shapes.Pill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary, contentColor = colorScheme.onPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(14.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = colorScheme.onPrimary
                    ) {}
                    Spacer(modifier = Modifier.width(spacing.Medium))
                    Text(
                        text = "Stop Alarm", fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.Medium))

            // Secondary Button: 🕒 Snooze 5 minutes
            Button(
                onClick = onSnooze,
                modifier = Modifier.fillMaxWidth().height(dimens.IconButtonSize),
                shape = shapes.Pill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondaryContainer,
                    contentColor = colorScheme.onSecondaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🕒", fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(spacing.Medium))
                    Text(
                        text = "Snooze 5 minutes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Preview(name = "Prayer Alarm Dialog Light Mode", showBackground = true)
@Composable
fun PrayerAlarmDialogLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        PrayerAlarmDialogContent(
            alarmData = ActiveAlarmData(
                prayerName = PrayerName.FAJR,
                timeFormatted = "05:47 AM"
            ),
            onStopAlarm = {},
            onSnooze = {}
        )
    }
}
@Preview(name = "Prayer Alarm Dialog Dark Mode", showBackground = true)
@Composable
fun PrayerAlarmDialogDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        PrayerAlarmDialogContent(
            alarmData = ActiveAlarmData(
                prayerName = PrayerName.FAJR,
                timeFormatted = "05:47 AM"
            ),
            onStopAlarm = {},
            onSnooze = {}
        )
    }
}

@Preview(name = "Prayer Alarm Dialog Preview")
@Composable
fun PrayerAlarmDialogPreview() {
    SolatNadiTheme {
        PrayerAlarmDialogContent(
            alarmData = ActiveAlarmData(
                prayerName = PrayerName.FAJR,
                timeFormatted = "05:47 AM"
            ),
            onStopAlarm = {},
            onSnooze = {}
        )
    }
}