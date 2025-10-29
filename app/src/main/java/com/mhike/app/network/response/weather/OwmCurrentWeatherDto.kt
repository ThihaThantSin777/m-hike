package com.mhike.app.network.response.weather

import com.squareup.moshi.Json

data class OwmCurrentWeatherDto(
    val coord: CoordDto?,
    val weather: List<WeatherDescDto>?,
    val base: String?,
    val main: MainDto?,
    val visibility: Int?,
    val wind: WindDto?,
    val clouds: CloudsDto?,
    val dt: Long?,
    val sys: SysDto?,
    val timezone: Int?,
    val id: Long?,
    val name: String?,
    val cod: Int?
)

data class CoordDto(val lon: Double?, val lat: Double?)

data class WeatherDescDto(
    val id: Int?,
    val main: String?,
    val description: String?,
    val icon: String?
)

data class MainDto(
    val temp: Double?,
    @Json(name = "feels_like") val feelsLike: Double?,
    @Json(name = "temp_min") val tempMin: Double?,
    @Json(name = "temp_max") val tempMax: Double?,
    val pressure: Int?,
    val humidity: Int?,
    @Json(name = "sea_level") val seaLevel: Int?,
    @Json(name = "grnd_level") val grndLevel: Int?
)

data class WindDto(
    val speed: Double?,
    val deg: Int?,
    val gust: Double?
)

data class CloudsDto(val all: Int?)

data class SysDto(
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?
)
