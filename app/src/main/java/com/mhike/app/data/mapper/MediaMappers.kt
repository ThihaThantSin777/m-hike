package com.mhike.app.data.mapper

import com.mhike.app.data.local.entity.MediaEntity
import com.mhike.app.domain.model.Media

fun MediaEntity.toDomain() = Media(id, hikeId, uri, mimeType, addedAt)
fun Media.toEntity() = MediaEntity(id, hikeId, uri, mimeType, addedAt)
