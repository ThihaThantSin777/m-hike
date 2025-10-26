package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Media
import com.mhike.app.domain.repo.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaForHike @Inject constructor(
    private val repo: MediaRepository
) {
    operator fun invoke(hikeId: Long): Flow<List<Media>> = repo.observeForHike(hikeId)
}
