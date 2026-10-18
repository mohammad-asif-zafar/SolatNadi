package com.hathway.solatnadi.data.mapper

import com.hathway.solatnadi.database.LocationEntity
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class MappersTest {

    @Test
    fun test12HourTimeFormatting() {
        assertEquals("01:22 PM", formatTo12Hour(13, 22))
        assertEquals("12:05 AM", formatTo12Hour(0, 5))
        assertEquals("12:00 PM", formatTo12Hour(12, 0))
        assertEquals("05:54 AM", formatTo12Hour(5, 54))
    }

    @Test
    fun testGregorianDateFormatting() {
        val date = LocalDate(2026, 10, 18)
        val formatted = formatGregorianDate(date)
        assertEquals("Sun, 18 Oct 2026", formatted)
    }

    @Test
    fun testCountryNameMapping() {
        assertEquals("Malaysia", getCountryName("MY"))
        assertEquals("Singapore", getCountryName("SG"))
        assertEquals("Saudi Arabia", getCountryName("SA"))
        assertEquals("United Arab Emirates", getCountryName("AE"))
        assertEquals("Indonesia", getCountryName("ID"))
        assertEquals("United Kingdom", getCountryName("GB"))
    }

    @Test
    fun testLocationEntityMapping() {
        val entity = LocationEntity(
            id = "kl_my",
            name = "Kuala Lumpur",
            country = "Malaysia",
            latitude = 3.1390,
            longitude = 101.6869,
            timezone = "Asia/Kuala_Lumpur",
            isSelected = 1L
        )

        val domain = entity.toDomain()

        assertEquals("kl_my", domain.id)
        assertEquals("Kuala Lumpur", domain.name)
        assertEquals(true, domain.isSelected)
    }
}
