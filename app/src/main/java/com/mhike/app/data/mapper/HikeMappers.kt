package com.mhike.app.data.mapper

import com.mhike.app.data.local.entity.HikeEntity
import com.mhike.app.domain.model.Hike

fun HikeEntity.toDomain(): Hike = Hike(
    id = id,
    name = name,
    location = location,
    date = date,
    parking = parking,
    lengthKm = lengthKm,
    difficulty = difficulty,
    description = description,
    terrain = terrain,
    expectedWeather = expectedWeather
)

fun Hike.toEntity(): HikeEntity = HikeEntity(
    id = id,
    name = name,
    location = location,
    date = date,
    parking = parking,
    lengthKm = lengthKm,
    difficulty = difficulty,
    description = description,
    terrain = terrain,
    expectedWeather = expectedWeather
)
