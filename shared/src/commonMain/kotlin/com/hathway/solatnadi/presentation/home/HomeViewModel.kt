package com.hathway.solatnadi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.usecase.GetNextPrayerUseCase
import com.hathway.solatnadi.domain.usecase.ObservePrayerTimesUseCase
import com.hathway.solatnadi.domain.usecase.RefreshPrayerTimesUseCase
import com.hathway.solatnadi.domain.usecase.ToggleNotificationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val observePrayerTimesUseCase: ObservePrayerTimesUseCase,
    private val refreshPrayerTimesUseCase: RefreshPrayerTimesUseCase,
    private val getNextPrayerUseCase: GetNextPrayerUseCase,
    private val toggleNotificationUseCase: ToggleNotificationUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

    private var countdownJob: Job? = null
    private var refreshJob: Job? = null

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                preferencesRepository.observeSelectedLocation(),
                preferencesRepository.observeAvailableLocations(),
                preferencesRepository.observeAppPreferences()
            ) { selectedLoc, locations, appPrefs ->
                Triple(selectedLoc, locations, appPrefs)
            }.distinctUntilChanged()
                .flatMapLatest { (location, locations, _) ->
                    val locationTz = try {
                        TimeZone.of(location.timezone)
                    } catch (e: Exception) {
                        TimeZone.currentSystemDefault()
                    }
                    val today = Clock.System.now().toLocalDateTime(locationTz).date

                    _uiState.update {
                        it.copy(
                            location = location,
                            availableLocations = locations,
                            isRefreshing = true
                        )
                    }

                    refreshJob?.cancel()
                    refreshJob = viewModelScope.launch {
                        val result = refreshPrayerTimesUseCase(location, today)
                        _uiState.update { current ->
                            current.copy(
                                isRefreshing = false,
                                isOffline = result.isFailure
                            )
                        }
                    }

                    observePrayerTimesUseCase(location.id, today)
                }.collect { schedule ->
                    if (schedule != null) {
                        _uiState.update { current ->
                            current.copy(
                                gregorianDateFormatted = schedule.gregorianDateFormatted,
                                hijriDateFormatted = schedule.hijriDateFormatted,
                                prayerTimes = schedule.prayerTimes
                            )
                        }
                        startCountdownTicker()
                    }
                }
        }
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Refresh -> {
                val loc = _uiState.value.location ?: return
                val locationTz = try {
                    TimeZone.of(loc.timezone)
                } catch (e: Exception) {
                    TimeZone.currentSystemDefault()
                }
                val today = Clock.System.now().toLocalDateTime(locationTz).date

                viewModelScope.launch {
                    _uiState.update { it.copy(isRefreshing = true) }
                    val result = refreshPrayerTimesUseCase(loc, today)
                    _uiState.update { it.copy(isRefreshing = false, isOffline = result.isFailure) }
                }
            }
            is HomeIntent.Retry -> {
                processIntent(HomeIntent.Refresh)
            }
            is HomeIntent.SelectLocation -> {
                viewModelScope.launch {
                    preferencesRepository.setSelectedLocation(intent.location)
                }
            }
            is HomeIntent.ToggleNotification -> {
                viewModelScope.launch {
                    toggleNotificationUseCase(intent.prayerName, intent.enabled)
                }
            }
        }
    }

    private fun startCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val currentTimes = _uiState.value.prayerTimes
                val currentLocation = _uiState.value.location
                if (currentTimes.isNotEmpty() && currentLocation != null) {
                    val locationTz = try {
                        TimeZone.of(currentLocation.timezone)
                    } catch (e: Exception) {
                        TimeZone.currentSystemDefault()
                    }
                    val now = Clock.System.now().toLocalDateTime(locationTz)
                    val nextInfo = getNextPrayerUseCase.execute(currentTimes, now)

                    if (nextInfo != null) {
                        val activeNextName = nextInfo.prayer.prayerName
                        val hasChangedNext = currentTimes.any { it.isNext != (it.prayerName == activeNextName) }

                        val updatedTimes = if (hasChangedNext) {
                            currentTimes.map { prayer ->
                                prayer.copy(isNext = prayer.prayerName == activeNextName)
                            }
                        } else {
                            currentTimes
                        }

                        _uiState.update { current ->
                            if (current.countdownFormatted != nextInfo.formattedCountdown || current.countdownProgress != nextInfo.progress || hasChangedNext) {
                                current.copy(
                                    prayerTimes = updatedTimes,
                                    nextPrayer = nextInfo.prayer,
                                    countdownFormatted = nextInfo.formattedCountdown,
                                    countdownProgress = nextInfo.progress
                                )
                            } else {
                                current
                            }
                        }
                    }
                }
                delay(1000)
            }
        }
    }
}
