package com.hathway.solatnadi.presentation.settings

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.AppTheme
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.TimeFormat
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLocationSelection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsScreenContent(
        uiState = uiState,
        onUpdatePreferences = viewModel::updatePreferences,
        onNavigateBack = onNavigateBack,
        onNavigateToLocationSelection = onNavigateToLocationSelection
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onUpdatePreferences: (AppPreferences) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLocationSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes
    val customColors = SolatNadiTheme.customColors

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val uriHandler = LocalUriHandler.current

    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

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
                text = "Settings",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(spacing.Medium))

        // ==========================================
        // LOCATION SECTION
        // ==========================================
        SettingsSectionHeader(title = "Location", icon = "📍")

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLocationSelection() },
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Current Location",
                        style = typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.selectedLocation?.name ?: "Kuala Lumpur"}, ${uiState.selectedLocation?.country ?: "Malaysia"}",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Text(
                    text = "Change ›",
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // ==========================================
        // NOTIFICATIONS SECTION
        // ==========================================
        SettingsSectionHeader(title = "Notifications", icon = "🔔")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            SettingsClickableRow(
                icon = "🔔",
                title = "Prayer Notifications",
                subtitle = "Sound, vibration, reminder time",
                onClick = { onNavigateToLocationSelection() }
            )
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // ==========================================
        // APPEARANCE SECTION
        // ==========================================
        SettingsSectionHeader(title = "Appearance", icon = "🎨")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Medium)) {
                // Theme item
                val themeLabel = when (uiState.preferences.appTheme) {
                    AppTheme.LIGHT -> "Light"
                    AppTheme.DARK -> "Dark"
                    AppTheme.SYSTEM -> "Light (System)"
                }

                SettingsClickableRow(
                    icon = "⚙️",
                    title = "Theme",
                    trailingText = themeLabel,
                    onClick = { showThemeDialog = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.Small),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // 12-Hour Format Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.Small, vertical = spacing.Small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "12-Hour Format",
                            style = typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = if (uiState.preferences.timeFormat == TimeFormat.FORMAT_12H) "Active (e.g. 05:54 AM)" else "24-Hour Active (e.g. 05:54)",
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = uiState.preferences.timeFormat == TimeFormat.FORMAT_12H,
                        onCheckedChange = { is12h ->
                            onUpdatePreferences(
                                uiState.preferences.copy(
                                    timeFormat = if (is12h) TimeFormat.FORMAT_12H else TimeFormat.FORMAT_24H
                                )
                            )
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.Small),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // ==========================================
        // MORE SECTION
        // ==========================================
        SettingsSectionHeader(title = "More", icon = "ℹ️")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(spacing.Medium)) {
                SettingsClickableRow(
                    icon = "ℹ️",
                    title = "About SolatNadi",
                    onClick = { showAboutDialog = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.Small),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                SettingsClickableRow(
                    icon = "💬",
                    title = "Help & Feedback",
                    onClick = { showHelpDialog = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.Small),
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                SettingsClickableRow(
                    icon = "🛡️",
                    title = "Data & Privacy",
                    onClick = { showPrivacyDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.XLarge))
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Select Theme",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    AppTheme.entries.forEach { theme ->
                        val label = when (theme) {
                            AppTheme.LIGHT -> "Light Mode"
                            AppTheme.DARK -> "Dark Mode"
                            AppTheme.SYSTEM -> "System Default"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdatePreferences(uiState.preferences.copy(appTheme = theme))
                                    showThemeDialog = false
                                }
                                .padding(vertical = spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.preferences.appTheme == theme,
                                onClick = {
                                    onUpdatePreferences(uiState.preferences.copy(appTheme = theme))
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(spacing.Small))
                            Text(
                                text = label,
                                style = typography.bodyLarge,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = colorScheme.primary)
                }
            }
        )
    }

    // About SolatNadi Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "About SolatNadi",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "SolatNadi v1.0.0",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(spacing.Small))
                    Text(
                        text = "SolatNadi is a modern, offline-first Islamic prayer times and Qibla application built with Kotlin Multiplatform and Jetpack Compose.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.Medium))
                    Text(
                        text = "🌐 Official Website:",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "https://solatnadi.app",
                        style = typography.bodyMedium,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri("https://solatnadi.app") } catch (_: Exception) {}
                        }
                    )
                    Spacer(modifier = Modifier.height(spacing.Small))
                    Text(
                        text = "👨‍💻 Developer Contact:",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "support@solatnadi.app",
                        style = typography.bodyMedium,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri("mailto:support@solatnadi.app") } catch (_: Exception) {}
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = colorScheme.primary)
                }
            }
        )
    }

    // Help & Feedback Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "Help & Feedback",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Have questions, feedback, or need assistance? Reach out directly to the developer:",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.Medium))
                    Text(
                        text = "📧 Email Support:",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "support@solatnadi.app",
                        style = typography.bodyLarge,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri("mailto:support@solatnadi.app") } catch (_: Exception) {}
                        }
                    )
                    Spacer(modifier = Modifier.height(spacing.Medium))
                    Text(
                        text = "🌐 Website & FAQ:",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "https://solatnadi.app",
                        style = typography.bodyMedium,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri("https://solatnadi.app") } catch (_: Exception) {}
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try { uriHandler.openUri("mailto:support@solatnadi.app") } catch (_: Exception) {}
                        showHelpDialog = false
                    }
                ) {
                    Text("Send Email", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close", color = colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Data & Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "Data & Privacy",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your privacy is important to us. SolatNadi is designed with an offline-first architecture:",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.Small))
                    Text(
                        text = "• Local Data Storage: All preferences and prayer schedules are stored locally in your device SQLDelight database.\n• No Personal Tracking: SolatNadi does not collect or sell any personal data.\n• Location Privacy: Location data is used strictly to compute accurate prayer times.",
                        style = typography.bodySmall,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.Medium))
                    Text(
                        text = "📄 Read Privacy Policy:",
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "https://solatnadi.app/privacy",
                        style = typography.bodyMedium,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri("https://solatnadi.app/privacy") } catch (_: Exception) {}
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        try { uriHandler.openUri("https://solatnadi.app/privacy") } catch (_: Exception) {}
                        showPrivacyDialog = false
                    }
                ) {
                    Text("Open Policy Link", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun SettingsClickableRow(
    icon: String,
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = spacing.Small, vertical = spacing.Medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = icon,
                style = typography.titleMedium,
                modifier = Modifier.padding(end = spacing.Medium)
            )
            Column {
                Text(
                    text = title,
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                if (!subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!trailingText.isNullOrEmpty()) {
                Text(
                    text = trailingText,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = spacing.Small)
                )
            }
            Text(
                text = "›",
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    icon: String? = null,
    modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = spacing.XSmall, bottom = spacing.Small)
    ) {
        if (!icon.isNullOrEmpty()) {
            Text(
                text = icon,
                style = typography.labelLarge,
                modifier = Modifier.padding(end = spacing.XSmall)
            )
        }
        Text(
            text = title,
            style = typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Preview(name = "Settings Light Mode", showBackground = true)
@Composable
fun SettingsScreenLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        SettingsScreenContent(
            uiState = SettingsUiState(
                selectedLocation = PrayerLocation(
                    "kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"
                )
            ),
            onUpdatePreferences = {},
            onNavigateBack = {},
            onNavigateToLocationSelection = {}
        )
    }
}

@Preview(name = "Settings Dark Mode", showBackground = true)
@Composable
fun SettingsScreenDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        SettingsScreenContent(
            uiState = SettingsUiState(
                selectedLocation = PrayerLocation(
                    "kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"
                )
            ),
            onUpdatePreferences = {},
            onNavigateBack = {},
            onNavigateToLocationSelection = {}
        )
    }
}