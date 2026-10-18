package com.hathway.solatnadi.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import com.hathway.solatnadi.domain.usecase.GetAppPreferencesUseCase
import com.hathway.solatnadi.domain.usecase.UpdateAppPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: AppPreferences = AppPreferences(),
    val availableLocations: List<PrayerLocation> = emptyList(),
    val selectedLocation: PrayerLocation? = null
)

class SettingsViewModel(
    private val getAppPreferencesUseCase: GetAppPreferencesUseCase,
    private val updateAppPreferencesUseCase: UpdateAppPreferencesUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getAppPreferencesUseCase(),
                preferencesRepository.observeAvailableLocations(),
                preferencesRepository.observeSelectedLocation()
            ) { prefs, locations, selected ->
                SettingsUiState(prefs, locations, selected)
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updatePreferences(newPreferences: AppPreferences) {
        viewModelScope.launch {
            updateAppPreferencesUseCase(newPreferences)
        }
    }

    fun selectLocation(location: PrayerLocation) {
        viewModelScope.launch {
            preferencesRepository.setSelectedLocation(location)
        }
    }
}
