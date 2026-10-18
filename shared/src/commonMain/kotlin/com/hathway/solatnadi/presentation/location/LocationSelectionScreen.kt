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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.settings.SettingsUiState
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode

@Composable
fun LocationSelectionScreen(
    viewModel: SettingsViewModel,
    onLocationSelected: (PrayerLocation) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LocationSelectionScreenContent(
        uiState = uiState,
        onLocationSelected = onLocationSelected,
        onNavigateBack = onNavigateBack
    )
}

/*@Composable
fun LocationSelectionScreenContent(
    uiState: SettingsUiState,
    onLocationSelected: (PrayerLocation) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(searchQuery, uiState.availableLocations) {
        if (searchQuery.isBlank()) {
            uiState.availableLocations
        } else {
            uiState.availableLocations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.country.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FBF9))
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

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
                text = "Select Your City",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SolatNadiPalette.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Search for your city to get accurate prayer times",
                fontSize = 14.sp,
                color = Color(0xFF687782)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search city or country...",
                        color = Color(0xFF9EA7B0),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Text("🔍", fontSize = 16.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F4F2),
                    unfocusedContainerColor = Color(0xFFF0F4F2),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Current Location",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SolatNadiPalette.TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val current = uiState.selectedLocation
            if (current != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEAF8EE)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📍", fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "${current.name}, ${current.country}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolatNadiPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${current.latitude}, ${current.longitude}",
                                fontSize = 13.sp,
                                color = Color(0xFF788590)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Popular Cities",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SolatNadiPalette.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        items(
            items = filteredLocations,
            key = { it.id }
        ) { loc ->
            val isSelected = loc.id == uiState.selectedLocation?.id
            val bg = if (isSelected) Color(0xFFEAF8EE) else Color.White
            val borderModifier = if (isSelected) {
                Modifier.border(1.5.dp, Color(0xFF8DD5B7), RoundedCornerShape(18.dp))
            } else Modifier

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(borderModifier)
                    .shadow(
                        elevation = if (isSelected) 1.dp else 2.dp,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onLocationSelected(loc) },
                shape = RoundedCornerShape(18.dp),
                color = bg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE2EFE7)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🕌", fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = loc.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolatNadiPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = loc.country,
                                fontSize = 13.sp,
                                color = Color(0xFF788590)
                            )
                        }
                    }

                    Text(
                        text = "›",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolatNadiPalette.ForestGreen
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}*/


@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun LocationSelectionScreenContent(
    uiState: SettingsUiState,
    onLocationSelected: (PrayerLocation) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLocations = remember(searchQuery, uiState.availableLocations) {
        if (searchQuery.isBlank()) {
            uiState.availableLocations
        } else {
            uiState.availableLocations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.country.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Pull design tokens from your Design System
    val spacing = SolatNadiTheme.spacing
    val dimens = SolatNadiTheme.dimens
    val shapes = SolatNadiTheme.shapes

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = dimens.ScreenHorizontalPadding)
    ) {
        item {
            Spacer(modifier = Modifier.height(spacing.Large))

            // Back Button Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavigateBack() }
                        .padding(spacing.Small),
                    color = Color.Transparent
                ) {
                    Text(
                        text = " ← ",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.Medium))

            Text(
                text = "Select Your City",
                style = typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.XSmall))

            Text(
                text = "Search for your city to get accurate prayer times",
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.Large))

            // Search Bar Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search city or country...",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    // Cleaner layout alternative to text emojis
                    Text("🔍", style = typography.bodyLarge)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = shapes.Medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surfaceVariant,
                    unfocusedContainerColor = colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(spacing.XXLarge))

            Text(
                text = "Current Location",
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.Small))

            // Active / Selected Location Card
            val current = uiState.selectedLocation
            if (current != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = dimens.CardElevation, shape = shapes.Medium),
                    shape = shapes.Medium,
                    color = colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.Large),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = shapes.Small,
                            color = colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📍", style = typography.titleLarge)
                            }
                        }

                        Spacer(modifier = Modifier.width(spacing.Medium))

                        Column {
                            Text(
                                text = "${current.name}, ${current.country}",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${current.latitude}, ${current.longitude}",
                                style = typography.bodySmall,
                                color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.XXLarge))

            Text(
                text = "Available Locations",
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.Small))
        }

        // Search Grid List Elements
        items(
            items = filteredLocations,
            key = { it.id }
        ) { loc ->
            val isSelected = loc.id == uiState.selectedLocation?.id

            val itemBackground = if (isSelected) colorScheme.primaryContainer else colorScheme.surface
            val rowTextColor = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface

            val borderModifier = if (isSelected) {
                Modifier.border(dimens.BorderWidth * 1.5f, colorScheme.primary, shapes.Medium)
            } else Modifier

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.XSmall)
                    .then(borderModifier)
                    .shadow(
                        elevation = if (isSelected) 1.dp else dimens.CardElevation,
                        shape = shapes.Medium
                    )
                    .clickable { onLocationSelected(loc) },
                shape = shapes.Medium,
                color = itemBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.Large, vertical = spacing.Medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = shapes.Small,
                            // FIXED: Using colorScheme directly or falling back cleanly to an explicit alpha layer calculation
                            color = if (isSelected) { colorScheme.primary.copy(alpha = 0.2f) } else { colorScheme.surfaceVariant }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("M", style = typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.width(spacing.Medium))

                        Column {
                            Text(
                                text = loc.name,
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = rowTextColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = loc.country,
                                style = typography.bodySmall,
                                color = if (isSelected) colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "›",
                        style = typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }
    }
}


@Preview(name = "City Selection Light Mode", showBackground = true)
@Composable
fun LocationSelectionLightPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.LIGHT) {
        LocationSelectionScreenContent(
            uiState = SettingsUiState(
                selectedLocation = PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                availableLocations = listOf(
                    PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                    PrayerLocation("sg", "Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
                    PrayerLocation("jakarta", "Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta")
                )
            ),
            onLocationSelected = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "City Selection Dark Mode", showBackground = true)
@Composable
fun LocationSelectionDarkPreview() {
    SolatNadiTheme(themeMode = SolatNadiThemeMode.DARK) {
        LocationSelectionScreenContent(
            uiState = SettingsUiState(
                selectedLocation = PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                availableLocations = listOf(
                    PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
                    PrayerLocation("sg", "Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
                    PrayerLocation("jakarta", "Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta")
                )
            ),
            onLocationSelected = {},
            onNavigateBack = {}
        )
    }
}
