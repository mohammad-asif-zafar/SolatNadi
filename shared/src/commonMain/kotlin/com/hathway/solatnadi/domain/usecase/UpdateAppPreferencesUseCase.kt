package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.repository.PreferencesRepository

class UpdateAppPreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(preferences: AppPreferences) {
        preferencesRepository.updateAppPreferences(preferences)
    }
}
