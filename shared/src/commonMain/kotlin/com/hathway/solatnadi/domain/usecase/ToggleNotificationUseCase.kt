package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.repository.PreferencesRepository

class ToggleNotificationUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(prayerName: PrayerName, enabled: Boolean) {
        preferencesRepository.toggleNotificationPreference(prayerName, enabled)
    }
}
