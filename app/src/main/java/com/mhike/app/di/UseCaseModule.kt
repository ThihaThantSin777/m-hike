package com.mhike.app.di

import com.mhike.app.domain.repo.HikeRepository
import com.mhike.app.domain.repo.MediaRepository
import com.mhike.app.domain.repo.ObservationRepository
import com.mhike.app.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides @Singleton
    fun provideCreateOrUpdateHike(repo: HikeRepository) = CreateOrUpdateHike(repo)

    @Provides @Singleton
    fun provideGetHikes(repo: HikeRepository) = GetHikes(repo)

    @Provides @Singleton
    fun provideDeleteHike(repo: HikeRepository) = DeleteHike(repo)

    @Provides @Singleton
    fun provideResetDatabase(repo: HikeRepository) = ResetDatabase(repo)

    @Provides @Singleton
    fun provideGetHikeById(repo: HikeRepository) = GetHikeById(repo)

    @Provides @Singleton
    fun provideSearchHikes(repo: HikeRepository) = SearchHikes(repo)

    // Observation use-cases
    @Provides @Singleton
    fun provideAddObservation(repo: ObservationRepository) = AddObservation(repo)

    @Provides @Singleton
    fun provideUpdateObservation(repo: ObservationRepository) = UpdateObservation(repo)

    @Provides @Singleton
    fun provideDeleteObservation(repo: ObservationRepository) = DeleteObservation(repo)

    @Provides @Singleton
    fun provideGetObservationsForHike(repo: ObservationRepository) = GetObservationsForHike(repo)

    @Provides @Singleton
    fun provideDeleteObservationsByHike(repo: ObservationRepository) = DeleteObservationsByHike(repo)


    @Provides @Singleton
    fun provideGetMediaForHike(repo: MediaRepository) = GetMediaForHike(repo)

    @Provides @Singleton
    fun provideAttachPhotoToHike(repo: MediaRepository) = AttachPhotoToHike(repo)
}
