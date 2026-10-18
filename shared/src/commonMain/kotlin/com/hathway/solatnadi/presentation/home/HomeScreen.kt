package com.hathway.solatnadi.presentation.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.presentation.components.CircularCountdownRing
import com.hathway.solatnadi.presentation.components.PrayerCard
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette
import org.jetbrains.compose.resources.painterResource
import solatnadi.shared.generated.resources.Res
import solatnadi.shared.generated.resources.misty_kuala_lumpur_sunrise_skyline_bg

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        onNavigateToSettings = onNavigateToSettings
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showLocationMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.misty_kuala_lumpur_sunrise_skyline_bg),
                contentDescription = "Kuala Lumpur Skyline Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x66FFFFFF),
                                Color(0xFFF9FBF9)
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLocationMenu = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = SolatNadiPalette.ForestGreen
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "📍",
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${uiState.location?.name ?: "Kuala Lumpur"}, ${uiState.location?.country ?: "Malaysia"}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolatNadiPalette.TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "∨",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolatNadiPalette.TextPrimary
                                )
                            }

                            Text(
                                text = "${uiState.gregorianDateFormatted}  •  ${uiState.hijriDateFormatted}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7A85)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showLocationMenu,
                        onDismissRequest = { showLocationMenu = false }
                    ) {
                        uiState.availableLocations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text("${loc.name}, ${loc.country}") },
                                onClick = {
                                    showLocationMenu = false
                                    onIntent(HomeIntent.SelectLocation(loc))
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { },
                        shape = CircleShape,
                        color = Color(0x33000000)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ℹ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SolatNadiPalette.TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToSettings() },
                        shape = CircleShape,
                        color = Color(0x33000000)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚙", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SolatNadiPalette.TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            CircularCountdownRing(
                countdownText = uiState.countdownFormatted,
                nextPrayerName = uiState.nextPrayer?.prayerName?.displayName ?: "Asr"
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isOffline) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3CD)
                ) {
                    Text(
                        text = "⚡ Working Offline — Displaying cached prayer schedule",
                        fontSize = 12.sp,
                        color = Color(0xFF856404),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            uiState.prayerTimes.forEach { prayerTime ->
                PrayerCard(
                    prayerTime = prayerTime,
                    onToggleNotification = { enabled ->
                        onIntent(HomeIntent.ToggleNotification(prayerTime.prayerName, enabled))
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
