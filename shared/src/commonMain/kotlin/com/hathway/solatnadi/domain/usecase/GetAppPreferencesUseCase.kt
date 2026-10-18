package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.AppPreferences
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetAppPreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<AppPreferences> {
        return preferencesRepository.observeAppPreferences()
    }
}
