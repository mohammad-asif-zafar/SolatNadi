package com.hathway.solatnadi.data.mapper

import com.hathway.solatnadi.database.LocationEntity
import com.hathway.solatnadi.database.PrayerTimeEntity
import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerSchedule
import com.hathway.solatnadi.domain.model.PrayerTime
import com.hathway.solatnadi.domain.model.TimeFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

fun LocationEntity.toDomain(): PrayerLocation {
    return PrayerLocation(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        isSelected = isSelected == 1L
    )
}

fun PrayerLocation.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        isSelected = if (isSelected) 1L else 0L
    )
}

fun PrayerTimeEntity.toDomainList(
    location: PrayerLocation,
    notificationPreferences: Map<PrayerName, Boolean> = emptyMap(),
    nextPrayerName: PrayerName? = null,
    timeFormat: TimeFormat = TimeFormat.FORMAT_12H
): PrayerSchedule {
    val dateParts = date.split("-")
    val localDate = if (dateParts.size == 3) {
        LocalDate(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
    } else {
        LocalDate(2026, 10, 18)
    }

    val times = listOf(
        createPrayerTime(PrayerName.FAJR, fajr, localDate, notificationPreferences[PrayerName.FAJR] ?: true, nextPrayerName, timeFormat),
        createPrayerTime(PrayerName.DHUHR, dhuhr, localDate, notificationPreferences[PrayerName.DHUHR] ?: true, nextPrayerName, timeFormat),
        createPrayerTime(PrayerName.ASR, asr, localDate, notificationPreferences[PrayerName.ASR] ?: true, nextPrayerName, timeFormat),
        createPrayerTime(PrayerName.MAGHRIB, maghrib, localDate, notificationPreferences[PrayerName.MAGHRIB] ?: true, nextPrayerName, timeFormat),
        createPrayerTime(PrayerName.ISHA, isha, localDate, notificationPreferences[PrayerName.ISHA] ?: true, nextPrayerName, timeFormat)
    )

    val nextPrayer = times.find { it.isNext } ?: times.find { it.prayerName == nextPrayerName }

    val gregorianFormatted = if (gregorianDate.isNotBlank()) gregorianDate else formatGregorianDate(localDate)
    val hijriFormatted = if (hijriDate.isNotBlank()) hijriDate else estimateHijriDate(localDate)

    return PrayerSchedule(
        location = location,
        date = localDate,
        gregorianDateFormatted = gregorianFormatted,
        hijriDateFormatted = hijriFormatted,
        prayerTimes = times,
        nextPrayer = nextPrayer,
        source = source
    )
}

private fun createPrayerTime(
    prayerName: PrayerName,
    timeRaw: String,
    date: LocalDate,
    isNotificationEnabled: Boolean,
    nextPrayerName: PrayerName?,
    timeFormat: TimeFormat
): PrayerTime {
    val cleanTime = timeRaw.split(" ").firstOrNull() ?: timeRaw
    val timeParts = cleanTime.split(":")
    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    @Suppress("DEPRECATION")
    val dateTime = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, minute, 0)
    val formattedTimeString = if (timeFormat == TimeFormat.FORMAT_24H) {
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    } else {
        formatTo12Hour(hour, minute)
    }

    return PrayerTime(
        prayerName = prayerName,
        timeString = formattedTimeString,
        scheduledDateTime = dateTime,
        isNotificationEnabled = isNotificationEnabled,
        isNext = prayerName == nextPrayerName
    )
}

fun formatTo12Hour(hour: Int, minute: Int): String {
    val period = if (hour >= 12) "PM" else "AM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "${hour12.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $period"
}

fun formatGregorianDate(date: LocalDate): String {
    val dayOfWeekStr = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val monthStr = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$dayOfWeekStr, ${date.dayOfMonth} $monthStr ${date.year}"
}

fun estimateHijriDate(date: LocalDate): String {
    return "7 Rabiulakhir 1448 AH"
}
