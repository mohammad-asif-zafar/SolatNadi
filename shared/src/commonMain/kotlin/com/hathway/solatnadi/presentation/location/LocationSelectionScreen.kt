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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiPalette

@Composable
fun LocationSelectionScreen(
    viewModel: SettingsViewModel,
    onLocationSelected: (PrayerLocation) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
}
