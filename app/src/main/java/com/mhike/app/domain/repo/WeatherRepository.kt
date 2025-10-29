package com.mhike.app.domain.repo

import com.mhike.app.domain.model.WeatherInfo

sealed class WeatherFailure(val message: String) {
    class NotFound(msg: String) : WeatherFailure(msg)
    class Network(msg: String) : WeatherFailure(msg)
    class Unknown(msg: String) : WeatherFailure(msg)
}

interface WeatherRepository {
    suspend fun currentWeatherForCity(city: String): Result<WeatherInfo>
    suspend fun currentWeatherForCoord(lat: Double, lon: Double): Result<WeatherInfo>
}
