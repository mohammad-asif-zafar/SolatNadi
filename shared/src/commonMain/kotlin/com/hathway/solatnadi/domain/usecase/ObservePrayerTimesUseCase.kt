package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerSchedule
import com.hathway.solatnadi.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class ObservePrayerTimesUseCase(
    private val prayerTimesRepository: PrayerTimesRepository
) {
    operator fun invoke(locationId: String, date: LocalDate): Flow<PrayerSchedule?> {
        return prayerTimesRepository.observePrayerTimes(locationId, date)
    }
}
