package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.repo.ObservationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetObservationById @Inject constructor(
    private val repo: ObservationRepository
) {
    operator fun invoke(id: Long): Flow<Observation?> = repo.observeById(id)
}
