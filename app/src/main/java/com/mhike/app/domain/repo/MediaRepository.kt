package com.mhike.app.domain.repo

import com.mhike.app.domain.model.Media
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeForHike(hikeId: Long): Flow<List<Media>>
    suspend fun insert(item: Media): Long
    suspend fun delete(item: Media)
}
