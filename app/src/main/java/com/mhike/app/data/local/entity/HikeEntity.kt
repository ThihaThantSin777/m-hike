package com.mhike.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

@Entity(tableName = "hikes")
data class HikeEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String,
    val date: LocalDate?,
    val parking: Boolean,
    val lengthKm: Double,
    val difficulty: String,
    val description: String? = null,

    val terrain: String? = null,
    val expectedWeather: String? = null,

    val createdAt: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
)
