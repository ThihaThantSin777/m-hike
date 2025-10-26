package com.mhike.app.domain.model

import kotlinx.datetime.LocalDate

data class Hike(
    val id: Long = 0,
    val name: String,
    val location: String,
    val date: LocalDate?,
    val parking: Boolean,
    val lengthKm: Double,
    val difficulty: String,
    val description: String? = null,
    val terrain: String? = null,
    val expectedWeather: String? = null
)
