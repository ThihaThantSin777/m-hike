package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.repo.ObservationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetObservationsForHike @Inject constructor(
    private val repo: ObservationRepository
) {
    operator fun invoke(hikeId: Long): Flow<List<Observation>> = repo.observeForHike(hikeId)
}
