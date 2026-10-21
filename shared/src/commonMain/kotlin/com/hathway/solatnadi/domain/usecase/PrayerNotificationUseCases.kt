package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.core.notification.NotificationScheduler
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerNotificationConfig
import com.hathway.solatnadi.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime

class ObservePrayerNotificationConfigsUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Map<PrayerName, PrayerNotificationConfig>> {
        return preferencesRepository.observePrayerNotificationConfigs()
    }
}

class GetPrayerNotificationConfigUseCase(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(prayerName: PrayerName): PrayerNotificationConfig {
        return preferencesRepository.getPrayerNotificationConfig(prayerName)
    }
}

class SavePrayerNotificationConfigUseCase(
    private val preferencesRepository: PreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) {
    @OptIn(kotlin.time.ExperimentalTime::class)
    suspend operator fun invoke(config: PrayerNotificationConfig) {
        preferencesRepository.savePrayerNotificationConfig(config)
        if (config.enabled) {
            val nowMs = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val nowDateTime = Instant.fromEpochMilliseconds(nowMs)
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            notificationScheduler.schedulePrayerNotification(
                prayerName = config.prayerName,
                scheduledTime = nowDateTime
            )
        } else {
            notificationScheduler.cancelPrayerNotification(config.prayerName)
        }
    }
}

class PlayTonePreviewUseCase(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke(soundKey: String) {
        notificationScheduler.playAzanPreview()
    }
}

class StopTonePreviewUseCase(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke() {
        notificationScheduler.stopAzanPreview()
    }
}

class TriggerTestNotificationUseCase(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke(prayerName: PrayerName = PrayerName.FAJR) {
        notificationScheduler.playAzanPreview()
    }
}
