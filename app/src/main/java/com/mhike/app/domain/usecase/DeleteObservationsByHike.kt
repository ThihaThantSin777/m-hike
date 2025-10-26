package com.mhike.app.domain.usecase

import com.mhike.app.domain.repo.ObservationRepository
import javax.inject.Inject

class DeleteObservationsByHike @Inject constructor(
    private val repo: ObservationRepository
) {
    suspend operator fun invoke(hikeId: Long) = repo.deleteByHike(hikeId)
}
