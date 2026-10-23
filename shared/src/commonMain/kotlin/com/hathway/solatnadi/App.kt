package com.hathway.solatnadi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hathway.solatnadi.core.notification.AlarmStateManager
import com.hathway.solatnadi.core.notification.NotificationScheduler
import com.hathway.solatnadi.domain.model.AppTheme
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.hathway.solatnadi.presentation.components.BackHandler
import com.hathway.solatnadi.presentation.components.PrayerAlarmDialog
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

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.presentation.notification.ChooseAlarmToneScreen
import com.hathway.solatnadi.presentation.notification.ChooseAlarmToneViewModel
import com.hathway.solatnadi.presentation.notification.PrayerNotificationDetailScreen
import com.hathway.solatnadi.presentation.notification.PrayerNotificationDetailViewModel
import com.hathway.solatnadi.presentation.notification.PrayerNotificationsListScreen
import com.hathway.solatnadi.presentation.notification.PrayerNotificationsListViewModel
import org.koin.core.parameter.parametersOf

sealed interface Screen {
    data object Splash : Screen
    data object Home : Screen
    data object Settings : Screen
    data object LocationSelection : Screen
    data class PrayerPreferences(val location: PrayerLocation) : Screen
    data object PrayerNotificationsList : Screen
    data class PrayerNotificationDetail(val prayerName: PrayerName) : Screen
    data class ChooseAlarmTone(val prayerName: PrayerName) : Screen
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
                    onNavigateToLocationSelection = { navigateTo(Screen.LocationSelection) },
                    onNavigateToPrayerNotificationsList = { navigateTo(Screen.PrayerNotificationsList) }
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
            is Screen.PrayerNotificationsList -> {
                val listViewModel: PrayerNotificationsListViewModel = koinInject()
                PrayerNotificationsListScreen(
                    viewModel = listViewModel,
                    onNavigateBack = { popBackStack() },
                    onNavigateToPrayerDetail = { prayerName ->
                        navigateTo(Screen.PrayerNotificationDetail(prayerName))
                    }
                )
            }
            is Screen.PrayerNotificationDetail -> {
                val detailViewModel: PrayerNotificationDetailViewModel = koinInject { parametersOf(screen.prayerName) }
                PrayerNotificationDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { popBackStack() },
                    onNavigateToChooseTone = { prayerName ->
                        navigateTo(Screen.ChooseAlarmTone(prayerName))
                    }
                )
            }
            is Screen.ChooseAlarmTone -> {
                val toneViewModel: ChooseAlarmToneViewModel = koinInject { parametersOf(screen.prayerName) }
                ChooseAlarmToneScreen(
                    viewModel = toneViewModel,
                    onNavigateBack = { popBackStack() }
                )
            }
        }

        val activeAlarmData by AlarmStateManager.activeAlarm.collectAsState()
        val scheduler = remember { NotificationScheduler() }

        activeAlarmData?.let { alarm ->
            PrayerAlarmDialog(
                alarmData = alarm,
                onStopAlarm = {
                    scheduler.stopAzanPreview()
                    AlarmStateManager.clearActiveAlarm()
                },
                onSnooze = {
                    scheduler.stopAzanPreview()
                    val nowInstant = Clock.System.now()
                    val snoozeInstant = nowInstant.plus(5.minutes)
                    val tz = TimeZone.currentSystemDefault()
                    val snoozeDateTime = snoozeInstant.toLocalDateTime(tz)
                    scheduler.schedulePrayerNotification(alarm.prayerName, snoozeDateTime, reminderMinutes = 0)
                    AlarmStateManager.clearActiveAlarm()
                },
                onDismissRequest = {
                    scheduler.stopAzanPreview()
                    AlarmStateManager.clearActiveAlarm()
                }
            )
        }
    }
}
