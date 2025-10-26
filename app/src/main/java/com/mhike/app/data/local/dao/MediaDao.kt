package com.mhike.app.data.local.dao

import androidx.room.*
import com.mhike.app.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaEntity): Long

    @Delete
    suspend fun delete(item: MediaEntity)

    @Query("SELECT * FROM media WHERE hikeId = :hikeId ORDER BY addedAt DESC")
    fun observeForHike(hikeId: Long): Flow<List<MediaEntity>>
}
