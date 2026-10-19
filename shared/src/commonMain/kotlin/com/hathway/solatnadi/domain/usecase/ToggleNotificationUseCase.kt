package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.core.notification.NotificationScheduler
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.domain.repository.PreferencesRepository

class ToggleNotificationUseCase(
    private val preferencesRepository: PreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(
        prayerTime: PrayerTime,
        enabled: Boolean
    ) {
        preferencesRepository.toggleNotificationPreference(prayerTime.prayerName, enabled)
        if (enabled) {
            notificationScheduler.schedulePrayerNotification(
                prayerName = prayerTime.prayerName,
                scheduledTime = prayerTime.scheduledDateTime
            )
            notificationScheduler.playAzanPreview()
        } else {
            notificationScheduler.cancelPrayerNotification(prayerTime.prayerName)
            notificationScheduler.stopAzanPreview()
        }
    }

    suspend operator fun invoke(
        prayerName: PrayerName,
        enabled: Boolean
    ) {
        preferencesRepository.toggleNotificationPreference(prayerName, enabled)
        if (enabled) {
            notificationScheduler.playAzanPreview()
        } else {
            notificationScheduler.cancelPrayerNotification(prayerName)
            notificationScheduler.stopAzanPreview()
        }
    }
}
