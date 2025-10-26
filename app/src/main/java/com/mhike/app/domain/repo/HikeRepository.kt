package com.mhike.app.domain.repo

import com.mhike.app.domain.model.Hike
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HikeRepository {
    fun observeAll(): Flow<List<Hike>>
    fun observeById(id: Long): Flow<Hike?>
    fun search(
        name: String?,
        location: String?,
        minLen: Double?,
        maxLen: Double?,
        start: LocalDate?,
        end: LocalDate?
    ): Flow<List<Hike>>

    suspend fun upsert(hike: Hike): Long
    suspend fun delete(hike: Hike)
    suspend fun reset()
}
