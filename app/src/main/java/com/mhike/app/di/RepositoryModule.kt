package com.mhike.app.di

import com.mhike.app.data.local.dao.HikeDao
import com.mhike.app.data.local.dao.MediaDao
import com.mhike.app.data.local.dao.ObservationDao
import com.mhike.app.data.repo.HikeRepositoryImpl
import com.mhike.app.data.repo.MediaRepositoryImpl
import com.mhike.app.data.repo.ObservationRepositoryImpl
import com.mhike.app.domain.repo.HikeRepository
import com.mhike.app.domain.repo.MediaRepository
import com.mhike.app.domain.repo.ObservationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHikeRepository(dao: HikeDao): HikeRepository =
        HikeRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideObservationRepository(dao: ObservationDao): ObservationRepository =
        ObservationRepositoryImpl(dao)

    @Provides @Singleton
    fun provideMediaRepository(dao: MediaDao): MediaRepository =
        MediaRepositoryImpl(dao)
}
