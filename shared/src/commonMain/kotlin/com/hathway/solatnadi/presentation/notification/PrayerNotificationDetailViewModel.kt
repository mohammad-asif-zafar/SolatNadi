package com.hathway.solatnadi.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.usecase.GetPrayerNotificationConfigUseCase
import com.hathway.solatnadi.domain.usecase.ObservePrayerTimesUseCase
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.usecase.SavePrayerNotificationConfigUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PrayerNotificationDetailUiState(
    val prayerName: PrayerName = PrayerName.FAJR,
    val timeFormatted: String = "05:54 AM",
    val config: PrayerNotificationConfig = PrayerNotificationConfig(PrayerName.FAJR),
    val isSaved: Boolean = false
)

class PrayerNotificationDetailViewModel(
    private val prayerName: PrayerName,
    private val getPrayerNotificationConfigUseCase: GetPrayerNotificationConfigUseCase,
    private val savePrayerNotificationConfigUseCase: SavePrayerNotificationConfigUseCase,
    private val observePrayerTimesUseCase: ObservePrayerTimesUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerNotificationDetailUiState(prayerName = prayerName))
    val uiState: StateFlow<PrayerNotificationDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val config = getPrayerNotificationConfigUseCase(prayerName)
            val selectedLocation = preferencesRepository.observeSelectedLocation().firstOrNull()

            var timeStr = "--:--"
            if (selectedLocation != null) {
                val tz = try { TimeZone.of(selectedLocation.timezone) } catch (_: Exception) { TimeZone.currentSystemDefault() }
                @OptIn(kotlin.time.ExperimentalTime::class)
                val today = Instant.fromEpochMilliseconds(kotlin.time.Clock.System.now().toEpochMilliseconds()).toLocalDateTime(tz).date
                val schedule = observePrayerTimesUseCase(selectedLocation.id, today).firstOrNull()
                timeStr = schedule?.prayerTimes?.find { it.prayerName == prayerName }?.timeString ?: "--:--"
            }

            _uiState.update {
                it.copy(
                    config = config,
                    timeFormatted = timeStr
                )
            }
        }
    }

    fun updateConfig(update: (PrayerNotificationConfig) -> PrayerNotificationConfig) {
        _uiState.update { current ->
            current.copy(config = update(current.config))
        }
    }

    fun saveSettings(onComplete: () -> Unit) {
        viewModelScope.launch {
            savePrayerNotificationConfigUseCase(_uiState.value.config)
            _uiState.update { it.copy(isSaved = true) }
            onComplete()
        }
    }
}
