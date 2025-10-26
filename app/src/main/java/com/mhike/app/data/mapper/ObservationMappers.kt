package com.mhike.app.data.mapper

import com.mhike.app.data.local.entity.ObservationEntity
import com.mhike.app.domain.model.Observation

fun ObservationEntity.toDomain(): Observation = Observation(
    id = id,
    hikeId = hikeId,
    text = text,
    at = at,
    comment = comment
)

fun Observation.toEntity(): ObservationEntity = ObservationEntity(
    id = id,
    hikeId = hikeId,
    text = text,
    at = at,
    comment = comment
)
