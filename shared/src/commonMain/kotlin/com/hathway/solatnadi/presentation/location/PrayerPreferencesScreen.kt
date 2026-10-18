package com.hathway.solatnadi.presentation.location

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.CalculationMethod
import com.hathway.solatnadi.domain.model.Madhab
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

@Composable
fun PrayerPreferencesScreen(
    targetLocation: PrayerLocation,
    viewModel: SettingsViewModel,
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedMethodId by remember(uiState.preferences) {
        mutableStateOf(uiState.preferences.calculationMethod)
    }
    var selectedMadhab by remember(uiState.preferences) {
        mutableStateOf(uiState.preferences.madhab)
    }
    var showMethodDropdown by remember { mutableStateOf(false) }

    val currentMethod = CalculationMethod.getById(selectedMethodId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onNavigateBack() }
                    .padding(8.dp),
                color = Color.Transparent
            ) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolatNadiPalette.TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Prayer Preferences",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SolatNadiPalette.TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Choose calculation settings for accurate prayer times",
            fontSize = 14.sp,
            color = Color(0xFF687782)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Calculation Method Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculation Method",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolatNadiPalette.TextPrimary
                    )
                    Text("ⓘ", fontSize = 18.sp, color = Color(0xFF687782))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFFE2E7E4), RoundedCornerShape(14.dp))
                            .clickable { showMethodDropdown = true },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFAFCFA)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentMethod.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolatNadiPalette.TextPrimary
                            )
                            Text("∨", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SolatNadiPalette.TextPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = showMethodDropdown,
                        onDismissRequest = { showMethodDropdown = false }
                    ) {
                        CalculationMethod.AVAILABLE_METHODS.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.name) },
                                onClick = {
                                    selectedMethodId = method.id
                                    showMethodDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF4F7F5)
                ) {
                    Text(
                        text = currentMethod.description,
                        fontSize = 13.sp,
                        color = Color(0xFF687782),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Madhab (Asr method) Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Madhab (Asr method)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SolatNadiPalette.TextPrimary
            )
            Text("ⓘ", fontSize = 18.sp, color = Color(0xFF687782))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Madhab.entries.forEach { madhabOption ->
                val isSelected = madhabOption == selectedMadhab
                val borderMod = if (isSelected) {
                    Modifier.border(2.dp, SolatNadiPalette.ForestGreen, RoundedCornerShape(18.dp))
                } else {
                    Modifier.border(1.dp, Color(0xFFE2E7E4), RoundedCornerShape(18.dp))
                }
                val cardBg = if (isSelected) Color(0xFFEAF8EE) else Color.White

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .then(borderMod)
                        .clickable { selectedMadhab = madhabOption },
                    shape = RoundedCornerShape(18.dp),
                    color = cardBg
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = if (isSelected) SolatNadiPalette.ForestGreen else Color(0xFFE0E5E2)
                        ) {
                            if (isSelected) {
                                Box(contentAlignment = Alignment.Center) {
                                    Surface(
                                        modifier = Modifier.size(8.dp),
                                        shape = CircleShape,
                                        color = Color.White
                                    ) {}
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = madhabOption.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolatNadiPalette.TextPrimary
                            )
                            Text(
                                text = madhabOption.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF788590)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF4F7F5)
        ) {
            Text(
                text = "Madhab affects the Asr prayer time only. Other prayer times remain the same.",
                fontSize = 13.sp,
                color = Color(0xFF687782),
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(30.dp))

        // Continue Button
        Button(
            onClick = {
                viewModel.selectLocation(targetLocation)
                viewModel.updatePreferences(
                    uiState.preferences.copy(
                        calculationMethod = selectedMethodId,
                        madhab = selectedMadhab
                    )
                )
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SolatNadiPalette.ForestGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
