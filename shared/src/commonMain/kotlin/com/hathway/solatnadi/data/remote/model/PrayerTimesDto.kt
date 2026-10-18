package com.hathway.solatnadi.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AladhanResponse(
    val code: Int,
    val status: String,
    val data: AladhanData? = null
)

@Serializable
data class AladhanData(
    val timings: TimingsDto,
    val date: DateInfoDto,
    val meta: MetaDto
)

@Serializable
data class TimingsDto(
    @SerialName("Fajr") val fajr: String,
    @SerialName("Dhuhr") val dhuhr: String,
    @SerialName("Asr") val asr: String,
    @SerialName("Maghrib") val maghrib: String,
    @SerialName("Isha") val isha: String
)

@Serializable
data class DateInfoDto(
    val readable: String,
    val timestamp: String,
    val gregorian: GregorianDateDto? = null,
    val hijri: HijriDateDto? = null
)

@Serializable
data class GregorianDateDto(
    val date: String,
    val weekday: WeekdayDto? = null,
    val month: MonthDto? = null,
    val year: String
)

@Serializable
data class HijriDateDto(
    val date: String,
    val day: String,
    val month: HijriMonthDto? = null,
    val year: String,
    val designation: DesignationDto? = null
)

@Serializable
data class DesignationDto(
    val abbreviated: String? = "AH",
    val expanded: String? = null
)

@Serializable
data class WeekdayDto(
    val en: String
)

@Serializable
data class MonthDto(
    val number: Int,
    val en: String
)

@Serializable
data class HijriMonthDto(
    val number: Int,
    val en: String,
    val ar: String? = null
)

@Serializable
data class MetaDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)
