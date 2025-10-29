package com.mhike.app.domain.model

data class WeatherInfo(
    val placeName: String,
    val countryCode: String?,
    val tempC: Double,
    val feelsLikeC: Double?,
    val tempMinC: Double?,
    val tempMaxC: Double?,
    val humidityPercent: Int?,
    val windSpeedMs: Double?,
    val windDeg: Int?,
    val windGustMs: Double?,
    val summary: String?,
    val description: String?,
    val iconCode: String?
)
