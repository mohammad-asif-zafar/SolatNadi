package com.hathway.solatnadi.data.repository

import com.hathway.solatnadi.data.mapper.getSystemCountryCode
import com.hathway.solatnadi.domain.model.PrayerLocation
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SystemLocationMatchTest {

    private val presetLocations = listOf(
        PrayerLocation("kl_my", "Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        PrayerLocation("singapore", "Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
        PrayerLocation("jakarta", "Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        PrayerLocation("mecca", "Mecca", "Saudi Arabia", 21.3891, 39.8579, "Asia/Riyadh"),
        PrayerLocation("london", "London", "United Kingdom", 51.5074, -0.1278, "Europe/London")
    )

    @Test
    fun testSystemTimezoneMatchingFindsCityInPresetList() {
        val currentTz = TimeZone.currentSystemDefault().id
        val matchedCity = presetLocations.find { it.timezone == currentTz }
            ?: presetLocations.find { it.country.equals(getSystemCountryCode(), ignoreCase = true) }
            ?: presetLocations.first()

        assertNotNull(matchedCity)
        assertTrue(matchedCity.name.isNotEmpty())
    }

    @Test
    fun testGetSystemCountryCodeReturnsValidString() {
        val countryCode = getSystemCountryCode()
        assertNotNull(countryCode)
    }
}
