package com.hathway.solatnadi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.hathway.solatnadi.domain.model.AppTheme
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.components.BackHandler
import com.hathway.solatnadi.presentation.home.HomeScreen
import com.hathway.solatnadi.presentation.home.HomeViewModel
import com.hathway.solatnadi.presentation.location.LocationSelectionScreen
import com.hathway.solatnadi.presentation.location.PrayerPreferencesScreen
import com.hathway.solatnadi.presentation.settings.SettingsScreen
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import com.hathway.solatnadi.presentation.theme.SolatNadiThemeMode
import org.koin.compose.koinInject

import com.hathway.solatnadi.presentation.splash.SplashScreen

sealed interface Screen {
    data object Splash : Screen
    data object Home : Screen
    data object Settings : Screen
    data object LocationSelection : Screen
    data class PrayerPreferences(val location: PrayerLocation) : Screen
}

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    val themeMode = when (settingsUiState.preferences.appTheme) {
        AppTheme.LIGHT -> SolatNadiThemeMode.LIGHT
        AppTheme.DARK -> SolatNadiThemeMode.DARK
        AppTheme.SYSTEM -> SolatNadiThemeMode.SYSTEM
    }

    SolatNadiTheme(themeMode = themeMode) {
        val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
        val currentScreen = backStack.lastOrNull() ?: Screen.Splash

        fun navigateTo(screen: Screen) {
            if (currentScreen != screen) {
                backStack.add(screen)
            }
        }

        fun popBackStack() {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        }

        fun popToHome() {
            while (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        }

        BackHandler(enabled = backStack.size > 1 && currentScreen != Screen.Splash) {
            popBackStack()
        }

        val homeViewModel: HomeViewModel = koinInject()

        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashComplete = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
                )
            }
            is Screen.Home -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSettings = { navigateTo(Screen.Settings) },
                    onNavigateToLocationSelection = { navigateTo(Screen.LocationSelection) }
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { popBackStack() },
                    onNavigateToLocationSelection = { navigateTo(Screen.LocationSelection) }
                )
            }
            is Screen.LocationSelection -> {
                LocationSelectionScreen(
                    viewModel = settingsViewModel,
                    onLocationSelected = { selectedLoc ->
                        navigateTo(Screen.PrayerPreferences(selectedLoc))
                    },
                    onNavigateBack = { popBackStack() }
                )
            }
            is Screen.PrayerPreferences -> {
                PrayerPreferencesScreen(
                    targetLocation = screen.location,
                    viewModel = settingsViewModel,
                    onComplete = { popToHome() },
                    onNavigateBack = { popBackStack() }
                )
            }
        }
    }
}
