package com.hathway.solatnadi.domain.usecase

import com.hathway.solatnadi.domain.model.PrayerName
import com.hathway.solatnadi.domain.model.PrayerTime
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetNextPrayerUseCaseTest {

    private val useCase = GetNextPrayerUseCase()

    private val prayerTimes = listOf(
        PrayerTime(PrayerName.FAJR, "05:54 AM", LocalDateTime(2026, 10, 18, 5, 54, 0)),
        PrayerTime(PrayerName.DHUHR, "01:22 PM", LocalDateTime(2026, 10, 18, 13, 22, 0)),
        PrayerTime(PrayerName.ASR, "04:46 PM", LocalDateTime(2026, 10, 18, 16, 46, 0)),
        PrayerTime(PrayerName.MAGHRIB, "07:21 PM", LocalDateTime(2026, 10, 18, 19, 21, 0)),
        PrayerTime(PrayerName.ISHA, "08:32 PM", LocalDateTime(2026, 10, 18, 20, 32, 0))
    )

    @Test
    fun testNextPrayerCalculatesAsrWhenCurrentTimeIsDhuhr() {
        val now = LocalDateTime(2026, 10, 18, 15, 21, 24)

        val nextInfo = useCase.execute(prayerTimes, now)

        assertNotNull(nextInfo)
        assertEquals(PrayerName.ASR, nextInfo.prayer.prayerName)
        assertEquals("01:24:36", nextInfo.formattedCountdown)
        assertTrue(nextInfo.progress > 0.5f && nextInfo.progress < 0.7f)
    }

    @Test
    fun testNextPrayerRollsOverToFajrWhenPastIsha() {
        val now = LocalDateTime(2026, 10, 18, 21, 30, 0)

        val nextInfo = useCase.execute(prayerTimes, now)

        assertNotNull(nextInfo)
        assertEquals(PrayerName.FAJR, nextInfo.prayer.prayerName)
        assertEquals("08:24:00", nextInfo.formattedCountdown)
        assertTrue(nextInfo.progress >= 0f)
    }

    @Test
    fun testNextPrayerCalculatesFajrEarlyMorningBeforeFajr() {
        val now = LocalDateTime(2026, 10, 18, 4, 0, 0)

        val nextInfo = useCase.execute(prayerTimes, now)

        assertNotNull(nextInfo)
        assertEquals(PrayerName.FAJR, nextInfo.prayer.prayerName)
        assertEquals("01:54:00", nextInfo.formattedCountdown)
    }

    @Test
    fun testEmptyListReturnsNull() {
        val now = LocalDateTime(2026, 10, 18, 12, 0, 0)
        val nextInfo = useCase.execute(emptyList(), now)
        assertNull(nextInfo)
    }
}
