package com.mhike.app.domain.repo

import com.mhike.app.domain.model.Observation
import kotlinx.coroutines.flow.Flow

interface ObservationRepository {
    fun observeForHike(hikeId: Long): Flow<List<Observation>>
    fun observeById(id: Long): Flow<Observation?>

    suspend fun insert(item: Observation): Long
    suspend fun update(item: Observation)
    suspend fun delete(item: Observation)
    suspend fun deleteByHike(hikeId: Long)
}
