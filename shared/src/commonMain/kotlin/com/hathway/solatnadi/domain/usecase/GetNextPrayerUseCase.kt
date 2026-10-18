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

        val upcomingIndex = sorted.indexOfFirst {
            val prayerSeconds = it.scheduledDateTime.hour * 3600L + it.scheduledDateTime.minute * 60L
            prayerSeconds > currentTotalSeconds
        }

        val targetPrayer: PrayerTime
        val previousPrayer: PrayerTime?
        val remainingSeconds: Long
        val totalIntervalSeconds: Long

        if (upcomingIndex != -1) {
            targetPrayer = sorted[upcomingIndex]
            previousPrayer = if (upcomingIndex > 0) sorted[upcomingIndex - 1] else null

            val targetSeconds = targetPrayer.scheduledDateTime.hour * 3600L + targetPrayer.scheduledDateTime.minute * 60L
            val prevSeconds = if (previousPrayer != null) {
                previousPrayer.scheduledDateTime.hour * 3600L + previousPrayer.scheduledDateTime.minute * 60L
            } else {
                val ishaSeconds = sorted.last().scheduledDateTime.hour * 3600L + sorted.last().scheduledDateTime.minute * 60L
                ishaSeconds - 86400L
            }

            remainingSeconds = targetSeconds - currentTotalSeconds
            totalIntervalSeconds = (targetSeconds - prevSeconds).coerceAtLeast(1L)
        } else {
            targetPrayer = sorted.first()
            previousPrayer = sorted.last()

            val fajrTomorrowSeconds = 86400L + targetPrayer.scheduledDateTime.hour * 3600L + targetPrayer.scheduledDateTime.minute * 60L
            val ishaTodaySeconds = previousPrayer.scheduledDateTime.hour * 3600L + previousPrayer.scheduledDateTime.minute * 60L

            remainingSeconds = fajrTomorrowSeconds - currentTotalSeconds
            totalIntervalSeconds = (fajrTomorrowSeconds - ishaTodaySeconds).coerceAtLeast(1L)
        }

        val elapsedSeconds = totalIntervalSeconds - remainingSeconds
        val progress = (elapsedSeconds.toFloat() / totalIntervalSeconds.toFloat()).coerceIn(0f, 1f)

        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60

        val formatted = "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

        return NextPrayerInfo(
            prayer = targetPrayer,
            remainingSeconds = remainingSeconds,
            formattedCountdown = formatted,
            progress = progress
        )
    }

    fun getNextPrayerName(
        prayerTimes: List<PrayerTime>,
        currentDateTime: LocalDateTime
    ): PrayerName? {
        return execute(prayerTimes, currentDateTime)?.prayer?.prayerName
    }
}
