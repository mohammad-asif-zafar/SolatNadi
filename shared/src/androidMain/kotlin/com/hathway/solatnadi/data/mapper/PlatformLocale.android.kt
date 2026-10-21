package com.hathway.solatnadi.data.mapper

import java.util.Locale

actual fun getSystemCountryCode(): String {
    return try {
        Locale.getDefault().country.uppercase()
    } catch (_: Exception) {
        ""
    }
}
