package com.hathway.solatnadi.data.repository

import com.hathway.solatnadi.domain.model.PrayerLocation
import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class PrayerTimesRepositoryTest {

    @Test
    fun testPrayerTimeMapping() {
        val location = PrayerLocation(
            id = "kl_my",
            name = "Kuala Lumpur",
            country = "Malaysia",
            latitude = 3.1390,
            longitude = 101.6869,
            timezone = "Asia/Kuala_Lumpur",
            isSelected = true
        )

        val prayerTime = PrayerTime(
            prayerName = PrayerName.ASR,
            timeString = "04:46 PM",
            scheduledDateTime = LocalDateTime(2026, 10, 18, 16, 46, 0),
            isNotificationEnabled = true,
            isNext = true
        )

        assertEquals(PrayerName.ASR, prayerTime.prayerName)
        assertEquals("04:46 PM", prayerTime.timeString)
        assertEquals(true, prayerTime.isNext)
        assertEquals(true, prayerTime.isNotificationEnabled)
        assertEquals("Kuala Lumpur", location.name)
    }
}
