package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.NextPrayerInfo
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import kotlinx.datetime.LocalDateTime

class GetNextPrayerUseCase {

    fun execute(
        prayerTimes: List<PrayerTime>,
        currentDateTime: LocalDateTime
    ): NextPrayerInfo? {
        if (prayerTimes.isEmpty()) return null

        val currentTotalSeconds = currentDateTime.hour * 3600L + currentDateTime.minute * 60L + currentDateTime.second

        val sorted = prayerTimes.sortedBy {
            it.scheduledDateTime.hour * 3600L + it.scheduledDateTime.minute * 60L
        }

        val upcoming = sorted.firstOrNull {
            val prayerSeconds = it.scheduledDateTime.hour * 3600L + it.scheduledDateTime.minute * 60L
            prayerSeconds > currentTotalSeconds
        }

        val targetPrayer = upcoming ?: sorted.first()
        val targetSeconds = targetPrayer.scheduledDateTime.hour * 3600L + targetPrayer.scheduledDateTime.minute * 60L

        val remainingSeconds = if (upcoming != null) {
            targetSeconds - currentTotalSeconds
        } else {
            (86400L - currentTotalSeconds) + targetSeconds
        }

        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60

        val formatted = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

        return NextPrayerInfo(
            prayer = targetPrayer,
            remainingSeconds = remainingSeconds,
            formattedCountdown = formatted
        )
    }

    fun getNextPrayerName(
        prayerTimes: List<PrayerTime>,
        currentDateTime: LocalDateTime
    ): PrayerName? {
        return execute(prayerTimes, currentDateTime)?.prayer?.prayerName
    }
}
