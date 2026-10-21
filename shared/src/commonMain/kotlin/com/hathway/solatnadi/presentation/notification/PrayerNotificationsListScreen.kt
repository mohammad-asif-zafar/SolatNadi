package com.hathway.solatnadi.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun PrayerNotificationsListScreen(
    viewModel: PrayerNotificationsListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPrayerDetail: (PrayerName) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    PrayerNotificationsListScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToPrayerDetail = onNavigateToPrayerDetail,
        onTestNotification = viewModel::toggleTestNotification
    )
}

@Composable
fun PrayerNotificationsListScreenContent(
    uiState: PrayerNotificationsListUiState,
    onNavigateBack: () -> Unit,
    onNavigateToPrayerDetail: (PrayerName) -> Unit,
    onTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes
    val customColors = SolatNadiTheme.customColors

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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

            Column {
                Text(
                    text = "Prayer Notifications",
                    style = typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Choose which prayers to configure",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Prayer Items List Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Small)) {
                uiState.prayerItems.forEachIndexed { index, item ->
                    val icon = when (item.prayerName) {
                        PrayerName.FAJR -> "🌅"
                        PrayerName.DHUHR -> "☀️"
                        PrayerName.ASR -> "🌤️"
                        PrayerName.MAGHRIB -> "🌅"
                        PrayerName.ISHA -> "🌙"
                    }

                    SettingsClickableRow(
                        icon = icon,
                        title = item.prayerName.displayName,
                        subtitle = item.timeFormatted,
                        trailingText = if (item.config.enabled) item.config.soundTone.title else "Disabled",
                        onClick = { onNavigateToPrayerDetail(item.prayerName) }
                    )

                    if (index < uiState.prayerItems.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = spacing.XSmall),
                            color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // Default Notification Settings Section
        SettingsSectionHeader(title = "Default notification settings", icon = "⚙️")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Medium)) {
                SettingsClickableRow(
                    icon = "🔊",
                    title = "Sound",
                    trailingText = uiState.defaultConfig.soundTone.title,
                    onClick = { onNavigateToPrayerDetail(PrayerName.FAJR) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.XSmall),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

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
                        checked = uiState.defaultConfig.vibrationEnabled,
                        onCheckedChange = { /* Default vibration toggle */ },
                        colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.XSmall),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                SettingsClickableRow(
                    icon = "🕒",
                    title = "Reminder before prayer",
                    trailingText = uiState.defaultConfig.reminderText,
                    onClick = { onNavigateToPrayerDetail(PrayerName.FAJR) }
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.XLarge))

        val isTesting = uiState.isTestingNotification
        val btnContainerColor = if (isTesting) colorScheme.errorContainer else colorScheme.primaryContainer
        val btnContentColor = if (isTesting) colorScheme.onErrorContainer else colorScheme.primary
        val btnText = if (isTesting) "⏸ Stop test notification" else "▶ Test notification"

        // Test Notification Action Button
        Button(
            onClick = onTestNotification,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = btnContainerColor,
                contentColor = btnContentColor
            )
        ) {
            Text(
                text = btnText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(spacing.XXLarge))
    }
}

@Preview(name = "Prayer Notifications Light Mode", showBackground = true)
@Composable
fun PrayerNotificationsListScreenLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        PrayerNotificationsListScreenContent(
            uiState = PrayerNotificationsListUiState(
                prayerItems = listOf(
                    PrayerNotificationItemState(PrayerName.FAJR, "05:54 AM", PrayerNotificationConfig(PrayerName.FAJR)),
                    PrayerNotificationItemState(PrayerName.DHUHR, "01:12 PM", PrayerNotificationConfig(PrayerName.DHUHR)),
                    PrayerNotificationItemState(PrayerName.ASR, "04:25 PM", PrayerNotificationConfig(PrayerName.ASR)),
                    PrayerNotificationItemState(PrayerName.MAGHRIB, "07:18 PM", PrayerNotificationConfig(PrayerName.MAGHRIB)),
                    PrayerNotificationItemState(PrayerName.ISHA, "08:29 PM", PrayerNotificationConfig(PrayerName.ISHA))
                )
            ),
            onNavigateBack = {},
            onNavigateToPrayerDetail = {},
            onTestNotification = {}
        )
    }
}

@Preview(name = "Prayer Notifications Dark Mode", showBackground = true)
@Composable
fun PrayerNotificationsListScreenDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        PrayerNotificationsListScreenContent(
            uiState = PrayerNotificationsListUiState(
                prayerItems = listOf(
                    PrayerNotificationItemState(PrayerName.FAJR, "05:54 AM", PrayerNotificationConfig(PrayerName.FAJR)),
                    PrayerNotificationItemState(PrayerName.DHUHR, "01:12 PM", PrayerNotificationConfig(PrayerName.DHUHR)),
                    PrayerNotificationItemState(PrayerName.ASR, "04:25 PM", PrayerNotificationConfig(PrayerName.ASR)),
                    PrayerNotificationItemState(PrayerName.MAGHRIB, "07:18 PM", PrayerNotificationConfig(PrayerName.MAGHRIB)),
                    PrayerNotificationItemState(PrayerName.ISHA, "08:29 PM", PrayerNotificationConfig(PrayerName.ISHA))
                )
            ),
            onNavigateBack = {},
            onNavigateToPrayerDetail = {},
            onTestNotification = {}
        )
    }
}
