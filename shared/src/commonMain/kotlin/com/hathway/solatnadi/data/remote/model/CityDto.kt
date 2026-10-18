package com.hathway.solatnadi.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class WorldCitiesResponse(
    val schemaVersion: String = "1.0.0",
    val cities: List<CityDto> = emptyList()
)

@Serializable
data class CityDto(
    val id: String,
    val name: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val population: Long? = null
)
