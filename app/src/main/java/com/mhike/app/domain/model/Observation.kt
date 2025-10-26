package com.mhike.app.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class Observation @OptIn(ExperimentalTime::class) constructor(
    val id: Long = 0,
    val hikeId: Long,
    val text: String,
    val at: Instant,
    val comment: String? = null
)
