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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.presentation.components.CircularCountdownRing
import com.hathway.solatnadi.presentation.components.PrayerCard
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource
import solatnadi.shared.generated.resources.Res
import solatnadi.shared.generated.resources.misty_kuala_lumpur_sunrise_skyline_bg

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToLocationSelection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToLocationSelection = onNavigateToLocationSelection
    )
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLocationSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pull central layout parameters from your Design System
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes
    val customColors = SolatNadiTheme.customColors

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Banner Image Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.misty_kuala_lumpur_sunrise_skyline_bg),
                contentDescription = "Skyline Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic background gradient that correctly respects theme mutations
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                colorScheme.background.copy(alpha = 0.4f),
                                colorScheme.background
                            )
                        )
                    )
            )
        }

        // Main Scrolling Feed Layout Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimens.ScreenHorizontalPadding,
                    vertical = dimens.ScreenVerticalPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Action Navigation Bar Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location Selector Dropdown Clickable Block
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shapes.Small)
                        .clickable { onNavigateToLocationSelection() }
                        .padding(horizontal = spacing.XSmall, vertical = spacing.XSmall)
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📍", style = typography.titleMedium)
                        }
                    }

                    Spacer(modifier = Modifier.width(spacing.Small))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${uiState.location?.name ?: "Kuala Lumpur"}, ${uiState.location?.country ?: "Malaysia"}",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(spacing.XSmall))
                            Text(
                                text = "▾",
                                style = typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${uiState.gregorianDateFormatted}  •  ${uiState.hijriDateFormatted}",
                            style = typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Header Secondary Option Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { },
                        shape = CircleShape,
                        color = colorScheme.onSurface.copy(alpha = 0.1f) // Translucent theme-neutral touch target
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("ℹ", style = typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.width(spacing.Small))

                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToSettings() },
                        shape = CircleShape,
                        color = colorScheme.onSurface.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚙", style = typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.XXLarge))

            // Dynamic Unified Countdown Arc Composable Widget
            CircularCountdownRing(
                countdownText = uiState.countdownFormatted,
                nextPrayerName = uiState.nextPrayer?.prayerName?.displayName ?: "Asr",
                progress = uiState.countdownProgress
            )

            Spacer(modifier = Modifier.height(spacing.XXLarge))

            // Adaptive Connection Network / Database Warning Status Banner
            if (uiState.isOffline) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.Medium),
                    shape = shapes.Small,
                    color = customColors.warning.copy(alpha = 0.15f) // Flips container to dark amber nicely
                ) {
                    Text(
                        text = "⚡ Working Offline — Displaying cached prayer schedule",
                        style = typography.bodySmall,
                        color = customColors.warning,
                        modifier = Modifier.padding(spacing.Medium),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Scrollable Prayer Card Iterative Layout Blocks
            uiState.prayerTimes.forEach { prayerTime ->
                key(prayerTime.prayerName) {
                    PrayerCard(
                        prayerTime = prayerTime,
                        onToggleNotification = { enabled ->
                            onIntent(HomeIntent.ToggleNotification(prayerTime.prayerName, enabled))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.Huge))
        }
    }
}

/*
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLocationSelection: () -> Unit
) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToLocationSelection() }
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
                nextPrayerName = uiState.nextPrayer?.prayerName?.displayName ?: "Asr",
                progress = uiState.countdownProgress,
                prayerName = uiState.nextPrayer?.prayerName
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
                key(prayerTime.prayerName) {
                    PrayerCard(
                        prayerTime = prayerTime,
                        onToggleNotification = { enabled ->
                            onIntent(HomeIntent.ToggleNotification(prayerTime.prayerName, enabled))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
*/

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun HomeScreenLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        HomeScreenContent(
            uiState = HomeUiState(
                location = PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                gregorianDateFormatted = "Sat, 18 Oct 2026",
                hijriDateFormatted = "7 Rabiulakhir 1448 AH",
                countdownFormatted = "01:24:36",
                countdownProgress = 0.58f,
                nextPrayer = PrayerTime(PrayerName.ASR, "04:46 PM", LocalDateTime(2026, 10, 18, 16, 46, 0), isNext = true),
                prayerTimes = listOf(
                    PrayerTime(PrayerName.FAJR, "05:54 AM", LocalDateTime(2026, 10, 18, 5, 54, 0)),
                    PrayerTime(PrayerName.DHUHR, "01:22 PM", LocalDateTime(2026, 10, 18, 13, 22, 0)),
                    PrayerTime(PrayerName.ASR, "04:46 PM", LocalDateTime(2026, 10, 18, 16, 46, 0), isNext = true),
                    PrayerTime(PrayerName.MAGHRIB, "07:21 PM", LocalDateTime(2026, 10, 18, 19, 21, 0)),
                    PrayerTime(PrayerName.ISHA, "08:32 PM", LocalDateTime(2026, 10, 18, 20, 32, 0))
                )
            ),
            onIntent = {},
            onNavigateToSettings = {},
            onNavigateToLocationSelection = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        HomeScreenContent(
            uiState = HomeUiState(
                location = PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                gregorianDateFormatted = "Sat, 18 Oct 2026",
                hijriDateFormatted = "7 Rabiulakhir 1448 AH",
                countdownFormatted = "01:24:36",
                countdownProgress = 0.58f,
                nextPrayer = PrayerTime(PrayerName.ASR, "04:46 PM", LocalDateTime(2026, 10, 18, 16, 46, 0), isNext = true),
                prayerTimes = listOf(
                    PrayerTime(PrayerName.FAJR, "05:54 AM", LocalDateTime(2026, 10, 18, 5, 54, 0)),
                    PrayerTime(PrayerName.DHUHR, "01:22 PM", LocalDateTime(2026, 10, 18, 13, 22, 0)),
                    PrayerTime(PrayerName.ASR, "04:46 PM", LocalDateTime(2026, 10, 18, 16, 46, 0), isNext = true),
                    PrayerTime(PrayerName.MAGHRIB, "07:21 PM", LocalDateTime(2026, 10, 18, 19, 21, 0)),
                    PrayerTime(PrayerName.ISHA, "08:32 PM", LocalDateTime(2026, 10, 18, 20, 32, 0))
                )
            ),
            onIntent = {},
            onNavigateToSettings = {},
            onNavigateToLocationSelection = {}
        )
    }
}
