package com.mhike.app.di

import android.content.Context
import androidx.room.Room
import com.mhike.app.data.local.MHikeDatabase
import com.mhike.app.data.local.dao.HikeDao
import com.mhike.app.data.local.dao.MediaDao
import com.mhike.app.data.local.dao.ObservationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideHikeDao(db: MHikeDatabase): HikeDao = db.hikeDao()

    @Provides
    fun provideObservationDao(db: MHikeDatabase): ObservationDao = db.observationDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MHikeDatabase =
        Room.databaseBuilder(context, MHikeDatabase::class.java, "mhike.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMediaDao(db: MHikeDatabase): MediaDao = db.mediaDao()

}
