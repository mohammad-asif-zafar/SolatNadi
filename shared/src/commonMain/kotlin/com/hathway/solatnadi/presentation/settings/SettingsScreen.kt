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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.TimeFormat
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLocationSelection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(8.dp),
                color = Color.Transparent
            ) {
                Text(
                    text = "← Back",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolatNadiPalette.ForestGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SolatNadiPalette.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSectionHeader(title = "Location")

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLocationSelection() },
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Location",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7A85)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.selectedLocation?.name ?: "Kuala Lumpur"}, ${uiState.selectedLocation?.country ?: "Malaysia"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolatNadiPalette.TextPrimary
                    )
                }

                Text(
                    text = "Change ›",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolatNadiPalette.ForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSectionHeader(title = "Preferences")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "12-Hour Format",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = SolatNadiPalette.TextPrimary
                        )
                        Text(
                            text = if (uiState.preferences.timeFormat == TimeFormat.FORMAT_12H) "Active (e.g. 05:54 AM)" else "24-Hour Active (e.g. 05:54)",
                            fontSize = 12.sp,
                            color = Color(0xFF788590)
                        )
                    }

                    Switch(
                        checked = uiState.preferences.timeFormat == TimeFormat.FORMAT_12H,
                        onCheckedChange = { is12h ->
                            viewModel.updatePreferences(
                                uiState.preferences.copy(
                                    timeFormat = if (is12h) TimeFormat.FORMAT_12H else TimeFormat.FORMAT_24H
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Automatic Location Detection",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = SolatNadiPalette.TextPrimary
                        )
                        Text(
                            text = "Use device GPS location when available",
                            fontSize = 12.sp,
                            color = Color(0xFF788590)
                        )
                    }

                    Switch(
                        checked = uiState.preferences.isAutoLocationEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.updatePreferences(
                                uiState.preferences.copy(isAutoLocationEnabled = enabled)
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSectionHeader(title = "About & Data Source")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Data Provider: Aladhan API / Configurable Provider",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SolatNadiPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Offline-First Architecture: Local SQLDelight Database Cache",
                    fontSize = 12.sp,
                    color = Color(0xFF788590)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "SolatNadi v1.0 • Built with KMP & Compose Multiplatform",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolatNadiPalette.ForestGreen
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF788590),
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}
