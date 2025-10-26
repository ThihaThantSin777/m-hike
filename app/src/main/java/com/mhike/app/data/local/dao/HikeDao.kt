package com.mhike.app.data.local.dao

import androidx.room.*
import com.mhike.app.data.local.entity.HikeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface HikeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(hike: HikeEntity): Long

    @Update
    suspend fun update(hike: HikeEntity)

    @Delete
    suspend fun delete(hike: HikeEntity)

    @Query("DELETE FROM hikes")
    suspend fun reset()

    @Query("SELECT * FROM hikes ORDER BY date DESC")
    fun observeAll(): Flow<List<HikeEntity>>

    @Query("SELECT * FROM hikes WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<HikeEntity?>

    // Basic + advanced search. Any null parameter is ignored.
    @Query("""
        SELECT * FROM hikes
        WHERE (:name IS NULL OR name LIKE :name || '%')
          AND (:location IS NULL OR location LIKE '%' || :location || '%')
          AND (:minLen IS NULL OR lengthKm >= :minLen)
          AND (:maxLen IS NULL OR lengthKm <= :maxLen)
          AND (:startDate IS NULL OR date >= :startDate)
          AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY date DESC
    """)
    fun search(
        name: String?,
        location: String?,
        minLen: Double?,
        maxLen: Double?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Flow<List<HikeEntity>>
}
