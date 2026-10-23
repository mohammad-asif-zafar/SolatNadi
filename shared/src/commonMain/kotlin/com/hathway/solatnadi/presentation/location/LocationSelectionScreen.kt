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
import org.jetbrains.compose.resources.stringResource
import solatnadi.shared.generated.resources.Res
import solatnadi.shared.generated.resources.*

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
                text = stringResource(Res.string.select_your_city),
                style = typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(spacing.XSmall))

            Text(
                text = stringResource(Res.string.search_city_msg),
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
                        text = stringResource(Res.string.search_hint),
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
                text = stringResource(Res.string.current_location),
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
                text = stringResource(Res.string.available_locations),
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
