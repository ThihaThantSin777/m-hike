package com.mhike.app.data.repo

import com.mhike.app.data.local.dao.HikeDao
import com.mhike.app.data.mapper.toDomain
import com.mhike.app.data.mapper.toEntity
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.repo.HikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class HikeRepositoryImpl @Inject constructor(
    private val dao: HikeDao
) : HikeRepository {

    override fun observeAll(): Flow<List<Hike>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Hike?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun search(
        name: String?, location: String?,
        minLen: Double?, maxLen: Double?,
        start: LocalDate?, end: LocalDate?
    ): Flow<List<Hike>> =
        dao.search(name, location, minLen, maxLen, start, end)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(hike: Hike): Long =
        dao.upsert(hike.toEntity())

    override suspend fun delete(hike: Hike) =
        dao.delete(hike.toEntity())

    override suspend fun reset() = dao.reset()
}
