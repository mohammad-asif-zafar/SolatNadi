package com.hathway.solatnadi.domain.model

data class PrayerLocation(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val isSelected: Boolean = false
)
