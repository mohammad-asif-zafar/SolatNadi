package com.hathway.solatnadi.data.mapper

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale

actual fun getSystemCountryCode(): String {
    return try {
        NSLocale.currentLocale.countryCode?.uppercase() ?: ""
    } catch (_: Exception) {
        ""
    }
}
