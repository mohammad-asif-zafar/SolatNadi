package com.hathway.solatnadi.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.usecase.ObservePrayerNotificationConfigsUseCase
import com.hathway.solatnadi.domain.usecase.ObservePrayerTimesUseCase
import com.hathway.solatnadi.domain.usecase.PlayTonePreviewUseCase
import com.hathway.solatnadi.domain.usecase.SavePrayerNotificationConfigUseCase
import com.hathway.solatnadi.domain.usecase.StopTonePreviewUseCase
import com.hathway.solatnadi.domain.usecase.TriggerTestNotificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PrayerNotificationItemState(
    val prayerName: PrayerName,
    val timeFormatted: String,
    val config: PrayerNotificationConfig
)

data class PrayerNotificationsListUiState(
    val prayerItems: List<PrayerNotificationItemState> = emptyList(),
    val defaultConfig: PrayerNotificationConfig = PrayerNotificationConfig(PrayerName.FAJR),
    val isTestingNotification: Boolean = false
)

class PrayerNotificationsListViewModel(
    private val observePrayerNotificationConfigsUseCase: ObservePrayerNotificationConfigsUseCase,
    private val observePrayerTimesUseCase: ObservePrayerTimesUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val savePrayerNotificationConfigUseCase: SavePrayerNotificationConfigUseCase,
    private val triggerTestNotificationUseCase: TriggerTestNotificationUseCase,
    private val playTonePreviewUseCase: PlayTonePreviewUseCase,
    private val stopTonePreviewUseCase: StopTonePreviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerNotificationsListUiState())
    val uiState: StateFlow<PrayerNotificationsListUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                preferencesRepository.observeSelectedLocation(),
                observePrayerNotificationConfigsUseCase()
            ) { location, configsMap ->
                val tz = try { TimeZone.of(location.timezone) } catch (_: Exception) { TimeZone.currentSystemDefault() }
                @OptIn(kotlin.time.ExperimentalTime::class)
                val today = Instant.fromEpochMilliseconds(kotlin.time.Clock.System.now().toEpochMilliseconds()).toLocalDateTime(tz).date

                observePrayerTimesUseCase(location.id, today).collect { schedule ->
                    val times = schedule?.prayerTimes ?: emptyList()
                    val items = PrayerName.entries.map { name ->
                        val prayerTime = times.find { it.prayerName == name }
                        val config = configsMap[name] ?: PrayerNotificationConfig(name)
                        PrayerNotificationItemState(
                            prayerName = name,
                            timeFormatted = prayerTime?.timeString ?: "--:--",
                            config = config
                        )
                    }

                    _uiState.update {
                        it.copy(
                            prayerItems = items,
                            defaultConfig = configsMap[PrayerName.FAJR] ?: PrayerNotificationConfig(PrayerName.FAJR)
                        )
                    }
                }
            }.collect {}
        }
    }

    fun togglePrayerNotification(prayerName: PrayerName, enabled: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.prayerItems.find { it.prayerName == prayerName }?.config
                ?: PrayerNotificationConfig(prayerName)
            savePrayerNotificationConfigUseCase(current.copy(enabled = enabled))
        }
    }

    fun toggleTestNotification() {
        if (_uiState.value.isTestingNotification) {
            stopTonePreviewUseCase()
            _uiState.update { it.copy(isTestingNotification = false) }
        } else {
            _uiState.update { it.copy(isTestingNotification = true) }
            val soundKey = _uiState.value.defaultConfig.soundKey
            playTonePreviewUseCase(soundKey)
            triggerTestNotificationUseCase(PrayerName.FAJR)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTonePreviewUseCase()
    }
}
