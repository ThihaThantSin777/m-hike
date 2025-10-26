package com.mhike.app.data.repo

import com.mhike.app.data.local.dao.ObservationDao
import com.mhike.app.data.mapper.toDomain
import com.mhike.app.data.mapper.toEntity
import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.repo.ObservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservationRepositoryImpl @Inject constructor(
    private val dao: ObservationDao
) : ObservationRepository {

    override fun observeForHike(hikeId: Long): Flow<List<Observation>> =
        dao.observeForHike(hikeId).map { it.map { e -> e.toDomain() } }

    override fun observeById(id: Long): Flow<Observation?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun insert(item: Observation): Long = dao.insert(item.toEntity())
    override suspend fun update(item: Observation) = dao.update(item.toEntity())
    override suspend fun delete(item: Observation) = dao.delete(item.toEntity())
    override suspend fun deleteByHike(hikeId: Long) = dao.deleteByHike(hikeId)
}
