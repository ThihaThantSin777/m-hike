package com.mhike.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mhike.app.data.local.dao.HikeDao
import com.mhike.app.data.local.dao.MediaDao
import com.mhike.app.data.local.dao.ObservationDao
import com.mhike.app.data.local.entity.HikeEntity
import com.mhike.app.data.local.entity.MediaEntity
import com.mhike.app.data.local.entity.ObservationEntity
import com.mhike.app.util.DateTimeConverters

@Database(
    entities = [
        HikeEntity::class,
        ObservationEntity::class,
        MediaEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class MHikeDatabase : RoomDatabase() {
    abstract fun hikeDao(): HikeDao
    abstract fun observationDao(): ObservationDao

    abstract fun mediaDao(): MediaDao
}
