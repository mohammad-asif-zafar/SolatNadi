package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.repository.PrayerTimesRepository
import kotlinx.datetime.LocalDate

class RefreshPrayerTimesUseCase(
    private val prayerTimesRepository: PrayerTimesRepository
) {
    suspend operator fun invoke(location: PrayerLocation, date: LocalDate): Result<Unit> {
        return prayerTimesRepository.refreshPrayerTimes(location, date)
    }
}
