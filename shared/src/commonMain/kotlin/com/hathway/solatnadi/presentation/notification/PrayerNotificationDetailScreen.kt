package com.hathway.solatnadi.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.presentation.settings.SettingsClickableRow
import com.hathway.solatnadi.presentation.settings.SettingsSectionHeader
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerNotificationDetailScreen(
    viewModel: PrayerNotificationDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChooseTone: (PrayerName) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showReminderBottomSheet by remember { mutableStateOf(false) }

    PrayerNotificationDetailScreenContent(
        uiState = uiState,
        onUpdateConfig = viewModel::updateConfig,
        onSaveSettings = { viewModel.saveSettings(onNavigateBack) },
        onNavigateBack = onNavigateBack,
        onOpenReminderBottomSheet = { showReminderBottomSheet = true },
        onNavigateToChooseTone = { onNavigateToChooseTone(uiState.prayerName) }
    )

    if (showReminderBottomSheet) {
        ReminderTimeBottomSheet(
            initialMinutes = uiState.config.reminderMinutes,
            onSelectMinutes = { newMinutes ->
                viewModel.updateConfig { it.copy(reminderMinutes = newMinutes) }
            },
            onDismiss = { showReminderBottomSheet = false }
        )
    }
}

@Composable
fun PrayerNotificationDetailScreenContent(
    uiState: PrayerNotificationDetailUiState,
    onUpdateConfig: ((PrayerNotificationConfig) -> PrayerNotificationConfig) -> Unit,
    onSaveSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    onOpenReminderBottomSheet: () -> Unit,
    onNavigateToChooseTone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes
    val customColors = SolatNadiTheme.customColors

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val prayerName = uiState.prayerName
    val config = uiState.config

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = dimens.ScreenHorizontalPadding,
                vertical = dimens.ScreenVerticalPadding
            )
    ) {
        // Navigation Header block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(vertical = spacing.Small, horizontal = spacing.XSmall)
            )

            Spacer(modifier = Modifier.width(spacing.Medium))

            Text(
                text = "${prayerName.displayName} Notification",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Master Switch Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.Large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (prayerName) {
                        PrayerName.FAJR -> "🌅"
                        PrayerName.DHUHR -> "☀️"
                        PrayerName.ASR -> "🌤️"
                        PrayerName.MAGHRIB -> "🌅"
                        PrayerName.ISHA -> "🌙"
                    }
                    Text(
                        text = icon,
                        style = typography.headlineMedium,
                        modifier = Modifier.padding(end = spacing.Medium)
                    )
                    Column {
                        Text(
                            text = prayerName.displayName,
                            style = typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = uiState.timeFormatted,
                            style = typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = config.enabled,
                    onCheckedChange = { enabled ->
                        onUpdateConfig { it.copy(enabled = enabled) }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Options Configuration Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Medium)) {
                // Reminder before prayer row
                SettingsClickableRow(
                    icon = "🕒",
                    title = "Reminder before prayer",
                    trailingText = config.reminderText,
                    onClick = onOpenReminderBottomSheet
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.XSmall),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Sound row
                SettingsClickableRow(
                    icon = "🎵",
                    title = "Sound",
                    trailingText = config.soundTone.title,
                    onClick = onNavigateToChooseTone
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.XSmall),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Vibration row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.Small, vertical = spacing.Medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📳",
                            style = typography.titleMedium,
                            modifier = Modifier.padding(end = spacing.Medium)
                        )
                        Text(
                            text = "Vibration",
                            style = typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                    }

                    Switch(
                        checked = config.vibrationEnabled,
                        onCheckedChange = { vib ->
                            onUpdateConfig { it.copy(vibrationEnabled = vib) }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.XSmall),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Repeat reminder row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.Small, vertical = spacing.Medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🔁",
                            style = typography.titleMedium,
                            modifier = Modifier.padding(end = spacing.Medium)
                        )
                        Column {
                            Text(
                                text = "Repeat reminder",
                                style = typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Repeat after 5 minutes if not dismissed",
                                style = typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = config.repeatReminderEnabled,
                        onCheckedChange = { rep ->
                            onUpdateConfig { it.copy(repeatReminderEnabled = rep) }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Notification Preview Section
        SettingsSectionHeader(title = "Notification preview", icon = "👁️")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Large)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        color = colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🕌",
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "SolatNadi",
                        style = typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = " • now",
                        style = typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val previewTitle = if (config.reminderMinutes > 0) {
                    "${prayerName.displayName} prayer in ${config.reminderText}"
                } else {
                    "It is time for ${prayerName.displayName} prayer"
                }

                val previewBody = if (config.reminderMinutes > 0) {
                    "Get ready for ${prayerName.displayName} (${uiState.timeFormatted})"
                } else {
                    "Time for ${prayerName.displayName} (${uiState.timeFormatted}). May Allah accept your prayer."
                }

                Text(
                    text = previewTitle,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = previewBody,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.XXLarge))

        // Save Settings Action Button
        Button(
            onClick = onSaveSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {
            Text(
                text = "💾 Save settings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(spacing.XXLarge))
    }
}

@Preview(name = "Prayer Notification Detail Light Mode", showBackground = true)
@Composable
fun PrayerNotificationDetailScreenLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        PrayerNotificationDetailScreenContent(
            uiState = PrayerNotificationDetailUiState(
                prayerName = PrayerName.FAJR,
                timeFormatted = "05:54 AM",
                config = PrayerNotificationConfig(
                    prayerName = PrayerName.FAJR,
                    enabled = true,
                    reminderMinutes = 10,
                    soundKey = "default_adhan",
                    vibrationEnabled = true,
                    repeatReminderEnabled = false
                )
            ),
            onUpdateConfig = {},
            onSaveSettings = {},
            onNavigateBack = {},
            onOpenReminderBottomSheet = {},
            onNavigateToChooseTone = {}
        )
    }
}

@Preview(name = "Prayer Notification Detail Dark Mode", showBackground = true)
@Composable
fun PrayerNotificationDetailScreenDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        PrayerNotificationDetailScreenContent(
            uiState = PrayerNotificationDetailUiState(
                prayerName = PrayerName.FAJR,
                timeFormatted = "05:54 AM",
                config = PrayerNotificationConfig(
                    prayerName = PrayerName.FAJR,
                    enabled = true,
                    reminderMinutes = 10,
                    soundKey = "default_adhan",
                    vibrationEnabled = true,
                    repeatReminderEnabled = false
                )
            ),
            onUpdateConfig = {},
            onSaveSettings = {},
            onNavigateBack = {},
            onOpenReminderBottomSheet = {},
            onNavigateToChooseTone = {}
        )
    }
}
