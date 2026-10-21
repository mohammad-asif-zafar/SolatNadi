package com.hathway.solatnadi.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.model.AdhanTone
import com.hathway.solatnadi.domain.model.AdhanTones
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.model.ToneCategory
import com.hathway.solatnadi.domain.usecase.GetPrayerNotificationConfigUseCase
import com.hathway.solatnadi.domain.usecase.PlayTonePreviewUseCase
import com.hathway.solatnadi.domain.usecase.SavePrayerNotificationConfigUseCase
import com.hathway.solatnadi.domain.usecase.StopTonePreviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChooseAlarmToneUiState(
    val prayerName: PrayerName = PrayerName.FAJR,
    val selectedCategory: ToneCategory = ToneCategory.ADHAN,
    val selectedSoundKey: String = "default_adhan",
    val currentlyPlayingKey: String? = null,
    val config: PrayerNotificationConfig = PrayerNotificationConfig(PrayerName.FAJR)
)

class ChooseAlarmToneViewModel(
    private val prayerName: PrayerName,
    private val getPrayerNotificationConfigUseCase: GetPrayerNotificationConfigUseCase,
    private val savePrayerNotificationConfigUseCase: SavePrayerNotificationConfigUseCase,
    private val playTonePreviewUseCase: PlayTonePreviewUseCase,
    private val stopTonePreviewUseCase: StopTonePreviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChooseAlarmToneUiState(prayerName = prayerName))
    val uiState: StateFlow<ChooseAlarmToneUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val config = getPrayerNotificationConfigUseCase(prayerName)
            _uiState.update {
                it.copy(
                    config = config,
                    selectedSoundKey = config.soundKey
                )
            }
        }
    }

    fun selectCategory(category: ToneCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectTone(tone: AdhanTone) {
        viewModelScope.launch {
            val updated = _uiState.value.config.copy(soundKey = tone.key)
            savePrayerNotificationConfigUseCase(updated)
            _uiState.update {
                it.copy(
                    config = updated,
                    selectedSoundKey = tone.key
                )
            }
        }
    }

    fun toggleAudioPreview(tone: AdhanTone) {
        if (_uiState.value.currentlyPlayingKey == tone.key) {
            stopTonePreviewUseCase()
            _uiState.update { it.copy(currentlyPlayingKey = null) }
        } else {
            playTonePreviewUseCase(tone.key)
            _uiState.update { it.copy(currentlyPlayingKey = tone.key) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTonePreviewUseCase()
    }
}
