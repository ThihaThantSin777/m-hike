package com.mhike.app.data.local.dao

import androidx.room.*
import com.mhike.app.data.local.entity.ObservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ObservationEntity): Long

    @Update
    suspend fun update(item: ObservationEntity)

    @Delete
    suspend fun delete(item: ObservationEntity)

    @Query("DELETE FROM observations WHERE hikeId = :hikeId")
    suspend fun deleteByHike(hikeId: Long)

    @Query("SELECT * FROM observations WHERE hikeId = :hikeId ORDER BY at DESC")
    fun observeForHike(hikeId: Long): Flow<List<ObservationEntity>>

    @Query("SELECT * FROM observations WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ObservationEntity?>
}
