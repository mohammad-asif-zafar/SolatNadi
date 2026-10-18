package com.hathway.solatnadi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.home.HomeScreen
import com.hathway.solatnadi.presentation.home.HomeViewModel
import com.hathway.solatnadi.presentation.location.LocationSelectionScreen
import com.hathway.solatnadi.presentation.location.PrayerPreferencesScreen
import com.hathway.solatnadi.presentation.settings.SettingsScreen
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import org.koin.compose.koinInject

sealed interface Screen {
    data object Home : Screen
    data object Settings : Screen
    data object LocationSelection : Screen
    data class PrayerPreferences(val location: PrayerLocation) : Screen
}

@Composable
fun App() {
    SolatNadiTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        val homeViewModel: HomeViewModel = koinInject()
        val settingsViewModel: SettingsViewModel = koinInject()

        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSettings = { currentScreen = Screen.Settings },
                    onNavigateToLocationSelection = { currentScreen = Screen.LocationSelection }
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { currentScreen = Screen.Home },
                    onNavigateToLocationSelection = { currentScreen = Screen.LocationSelection }
                )
            }
            is Screen.LocationSelection -> {
                LocationSelectionScreen(
                    viewModel = settingsViewModel,
                    onLocationSelected = { selectedLoc ->
                        currentScreen = Screen.PrayerPreferences(selectedLoc)
                    },
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
            is Screen.PrayerPreferences -> {
                PrayerPreferencesScreen(
                    targetLocation = screen.location,
                    viewModel = settingsViewModel,
                    onComplete = { currentScreen = Screen.Home },
                    onNavigateBack = { currentScreen = Screen.LocationSelection }
                )
            }
        }
    }
}
