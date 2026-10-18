package com.hathway.solatnadi.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.model.PrayerLocation
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
import kotlinx.datetime.LocalDate
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

    init {
        observeData()
    }

    private fun observeData() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        viewModelScope.launch {
            combine(
                preferencesRepository.observeSelectedLocation(),
                preferencesRepository.observeAvailableLocations()
            ) { selectedLoc, locations ->
                selectedLoc to locations
            }.distinctUntilChanged()
                .flatMapLatest { (location, locations) ->
                    _uiState.update { it.copy(location = location, availableLocations = locations) }
                    refreshPrayerTimes(location, today)
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
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                viewModelScope.launch {
                    _uiState.update { it.copy(isRefreshing = true) }
                    refreshPrayerTimes(loc, today)
                    _uiState.update { it.copy(isRefreshing = false) }
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

    private suspend fun refreshPrayerTimes(location: PrayerLocation, date: LocalDate) {
        val result = refreshPrayerTimesUseCase(location, date)
        if (result.isFailure) {
            _uiState.update { it.copy(isOffline = true) }
        } else {
            _uiState.update { it.copy(isOffline = false, errorMessage = null) }
        }
    }

    private fun startCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val currentTimes = _uiState.value.prayerTimes
                if (currentTimes.isNotEmpty()) {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val nextInfo = getNextPrayerUseCase.execute(currentTimes, now)

                    if (nextInfo != null) {
                        val updatedTimes = currentTimes.map { prayer ->
                            prayer.copy(isNext = prayer.prayerName == nextInfo.prayer.prayerName)
                        }
                        _uiState.update {
                            it.copy(
                                prayerTimes = updatedTimes,
                                nextPrayer = nextInfo.prayer,
                                countdownFormatted = nextInfo.formattedCountdown
                            )
                        }
                    }
                }
                delay(1000)
            }
        }
    }
}
