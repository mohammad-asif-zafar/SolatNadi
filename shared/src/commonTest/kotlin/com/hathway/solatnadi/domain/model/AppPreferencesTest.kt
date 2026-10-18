package com.hathway.solatnadi.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AppPreferencesTest {

    @Test
    fun testAppPreferencesDefaults() {
        val prefs = AppPreferences()
        assertEquals(3, prefs.calculationMethod)
        assertEquals(Madhab.SHAFI, prefs.madhab)
        assertEquals(TimeFormat.FORMAT_12H, prefs.timeFormat)
        assertEquals(AppTheme.LIGHT, prefs.appTheme)
        assertEquals(false, prefs.isAutoLocationEnabled)
    }

    @Test
    fun testCalculationMethodLookup() {
        val jakim = CalculationMethod.getById(3)
        assertEquals("JAKIM (Malaysia)", jakim.name)

        val isna = CalculationMethod.getById(2)
        assertEquals("ISNA (North America)", isna.name)

        val default = CalculationMethod.getById(99)
        assertEquals("JAKIM (Malaysia)", default.name)
    }

    @Test
    fun testMadhabCodeMapping() {
        assertEquals(0, Madhab.SHAFI.schoolCode)
        assertEquals(1, Madhab.HANAFI.schoolCode)
    }
}
