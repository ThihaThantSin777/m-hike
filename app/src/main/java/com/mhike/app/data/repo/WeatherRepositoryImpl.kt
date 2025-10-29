package com.mhike.app.data.repo

import com.mhike.app.BuildConfig
import com.mhike.app.domain.model.WeatherInfo
import com.mhike.app.domain.repo.WeatherRepository
import com.mhike.app.network.api.WeatherApi
import com.mhike.app.network.response.weather.OwmCurrentWeatherDto
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    private val apiKey = BuildConfig.OWM_API_KEY

    override suspend fun currentWeatherForCity(city: String): Result<WeatherInfo> {
        return try {
            val response = api.getCurrentWeatherByCity(
                city = city,
                apiKey = apiKey
            )
            Result.success(response.toWeatherInfo())
        } catch (e: Exception) {
            Result.failure(
                Exception("Failed to fetch weather for $city: ${e.message}")
            )
        }
    }

    override suspend fun currentWeatherForCoord(lat: Double, lon: Double): Result<WeatherInfo> {
        return try {
            val response = api.getCurrentWeatherByCoord(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            )
            Result.success(response.toWeatherInfo())
        } catch (e: Exception) {
            Result.failure(
                Exception("Failed to fetch weather for coordinates: ${e.message}")
            )
        }
    }

    private fun OwmCurrentWeatherDto.toWeatherInfo(): WeatherInfo {
        return WeatherInfo(
            placeName = this.name ?: "Unknown Location",
            countryCode = this.sys?.country,
            tempC = this.main?.temp ?: 0.0,
            feelsLikeC = this.main?.feelsLike,
            tempMinC = this.main?.tempMin,
            tempMaxC = this.main?.tempMax,
            humidityPercent = this.main?.humidity,
            windSpeedMs = this.wind?.speed,
            windDeg = this.wind?.deg,
            windGustMs = this.wind?.gust,
            summary = this.weather?.firstOrNull()?.main,
            description = this.weather?.firstOrNull()?.description,
            iconCode = this.weather?.firstOrNull()?.icon
        )
    }
}