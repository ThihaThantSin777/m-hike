package com.mhike.app.data.repo

import com.mhike.app.data.local.dao.MediaDao
import com.mhike.app.data.mapper.toDomain
import com.mhike.app.data.mapper.toEntity
import com.mhike.app.domain.model.Media
import com.mhike.app.domain.repo.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val dao: MediaDao
) : MediaRepository {
    override fun observeForHike(hikeId: Long): Flow<List<Media>> =
        dao.observeForHike(hikeId).map { it.map { e -> e.toDomain() } }

    override suspend fun insert(item: Media): Long = dao.insert(item.toEntity())
    override suspend fun delete(item: Media) = dao.delete(item.toEntity())
}
