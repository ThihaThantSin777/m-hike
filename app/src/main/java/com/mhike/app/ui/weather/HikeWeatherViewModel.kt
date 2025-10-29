package com.mhike.app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.WeatherInfo
import com.mhike.app.domain.repo.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val data: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

@HiltViewModel
class HikeWeatherViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    fun loadByCity(place: String) {

        if (place.isBlank()) {
            _state.value = WeatherUiState.Error("No location provided.")
            return
        }

        val trimmedPlace = place.trim()
        if (trimmedPlace.length < 2) {
            _state.value = WeatherUiState.Error("Location name is too short.")
            return
        }

        viewModelScope.launch {
            _state.value = WeatherUiState.Loading
            try {
                val res = repo.currentWeatherForCity(trimmedPlace)
                _state.value = res.fold(
                    onSuccess = { WeatherUiState.Success(it) },
                    onFailure = { error ->
                        WeatherUiState.Error(
                            when {
                                error.message?.contains("404") == true ->
                                    "Location '$trimmedPlace' not found"
                                error.message?.contains("401") == true ->
                                    "API key error. Please check configuration."
                                error.message?.contains("timeout") == true ->
                                    "Request timed out. Please check your connection."
                                else -> error.message ?: "Failed to load weather data"
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = WeatherUiState.Error(
                    "Unexpected error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun loadByCoord(lat: Double, lon: Double) {
        if (!isValidLatitude(lat)) {
            _state.value = WeatherUiState.Error(
                "Invalid latitude: $lat (must be between -90 and 90)"
            )
            return
        }

        if (!isValidLongitude(lon)) {
            _state.value = WeatherUiState.Error(
                "Invalid longitude: $lon (must be between -180 and 180)"
            )
            return
        }

        viewModelScope.launch {
            _state.value = WeatherUiState.Loading
            try {
                val res = repo.currentWeatherForCoord(lat, lon)
                _state.value = res.fold(
                    onSuccess = { WeatherUiState.Success(it) },
                    onFailure = { error ->
                        WeatherUiState.Error(
                            when {
                                error.message?.contains("404") == true ->
                                    "Weather data not found for coordinates ($lat, $lon)"
                                error.message?.contains("401") == true ->
                                    "API key error. Please check configuration."
                                error.message?.contains("timeout") == true ->
                                    "Request timed out. Please check your connection."
                                else -> error.message ?: "Failed to load weather data"
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = WeatherUiState.Error(
                    "Unexpected error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }


    fun refresh() {
        val currentState = _state.value
        if (currentState is WeatherUiState.Success) {
            val weather = currentState.data
            loadByCity(weather.placeName)
        }
    }

    fun resetState() {
        _state.value = WeatherUiState.Idle
    }


    private fun isValidLatitude(lat: Double): Boolean {
        return lat in -90.0..90.0
    }


    private fun isValidLongitude(lon: Double): Boolean {
        return lon in -180.0..180.0
    }
}