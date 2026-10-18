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
import com.hathway.solatnadi.domain.model.AppPreferences
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

    Column(
        modifier = modifier.fillMaxSize()
            .background(colorScheme.background) // Gracefully flips from light green-tint to deep dark teal
            .statusBarsPadding().verticalScroll(rememberScrollState()).padding(
                horizontal = dimens.ScreenHorizontalPadding, vertical = dimens.ScreenVerticalPadding
            )
    ) {
        // Navigation Header block
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = spacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier.clickable { onNavigateBack() }
                    .padding(vertical = spacing.Small, horizontal = spacing.XSmall))

            Spacer(modifier = Modifier.width(spacing.Medium))

            Text(
                text = "Settings",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(spacing.XLarge))

        // ==========================================
        // LOCATION CARD SECTION
        // ==========================================
        SettingsSectionHeader(title = "Location")

        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onNavigateToLocationSelection() },
            shape = shapes.Medium,
            color = colorScheme.surface,
            tonalElevation = 1.dp // subtle elevation adjustment for Material 3 design spec
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(spacing.Large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
        // PREFERENCES CARD SECTION
        // ==========================================
        SettingsSectionHeader(title = "Preferences")

        Surface(
            modifier = Modifier.fillMaxWidth(), shape = shapes.Medium, color = colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(spacing.Large)) {
                // Row 1: Time Format Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(spacing.Large))

                // Row 2: GPS Configuration Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automatic Location Detection",
                            style = typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Use device GPS location when available",
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = uiState.preferences.isAutoLocationEnabled,
                        onCheckedChange = { enabled ->
                            onUpdatePreferences(
                                uiState.preferences.copy(isAutoLocationEnabled = enabled)
                            )
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = customColors.success)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.Large))

        // ==========================================
        // SYSTEM METRICS & BRAND DATA CARD SECTION
        // ==========================================
        SettingsSectionHeader(title = "About & Data Source")

        Surface(
            modifier = Modifier.fillMaxWidth(), shape = shapes.Medium, color = colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(spacing.Large)) {
                Text(
                    text = "Data Provider: Aladhan API / Configurable Provider",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Offline-First Architecture: Local SQLDelight Database Cache",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.Medium))
                Text(
                    text = "SolatNadi v1.0 • Built with KMP & Compose Multiplatform",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String, modifier: Modifier = Modifier
) {
    val spacing = SolatNadiTheme.spacing
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = title,
        style = typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        modifier = modifier.padding(start = spacing.XSmall, bottom = spacing.Small)
    )
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
            ), onUpdatePreferences = {}, onNavigateBack = {}, onNavigateToLocationSelection = {})
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
            ), onUpdatePreferences = {}, onNavigateBack = {}, onNavigateToLocationSelection = {})
    }
}
