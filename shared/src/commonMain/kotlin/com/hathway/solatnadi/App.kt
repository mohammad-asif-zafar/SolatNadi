package com.hathway.solatnadi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hathway.solatnadi.presentation.home.HomeScreen
import com.hathway.solatnadi.presentation.home.HomeViewModel
import com.hathway.solatnadi.presentation.settings.SettingsScreen
import com.hathway.solatnadi.presentation.settings.SettingsViewModel
import com.hathway.solatnadi.presentation.theme.SolatNadiTheme
import org.koin.compose.koinInject

sealed interface Screen {
    data object Home : Screen
    data object Settings : Screen
}

@Composable
fun App() {
    SolatNadiTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        val homeViewModel: HomeViewModel = koinInject()
        val settingsViewModel: SettingsViewModel = koinInject()

        when (currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToSettings = { currentScreen = Screen.Settings }
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
        }
    }
}
