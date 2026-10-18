package com.hathway.solatnadi.data.remote

import com.hathway.solatnadi.data.remote.model.AladhanResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class PrayerTimesApi(
    private val httpClient: HttpClient = createDefaultHttpClient()
) {
    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        method: Int = 3
    ): AladhanResponse {
        val dateString = "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthNumber.toString().padStart(2, '0')}-${date.year}"
        return httpClient.get("https://api.aladhan.com/v1/timings/$dateString") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("method", method)
        }.body()
    }

    suspend fun getPrayerTimesByCity(
        city: String,
        country: String,
        date: LocalDate,
        method: Int = 3
    ): AladhanResponse {
        val dateString = "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthNumber.toString().padStart(2, '0')}-${date.year}"
        return httpClient.get("https://api.aladhan.com/v1/timingsByCity/$dateString") {
            parameter("city", city)
            parameter("country", country)
            parameter("method", method)
        }.body()
    }
}

fun createDefaultHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
}
