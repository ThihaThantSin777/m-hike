package com.mhike.app.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class Media @OptIn(ExperimentalTime::class) constructor(
    val id: Long = 0,
    val hikeId: Long,
    val uri: String,
    val mimeType: String? = null,
    val addedAt: Instant
)
