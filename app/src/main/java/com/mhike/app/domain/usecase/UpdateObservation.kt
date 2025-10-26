package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.repo.ObservationRepository
import javax.inject.Inject

class UpdateObservation @Inject constructor(
    private val repo: ObservationRepository
) {
    suspend operator fun invoke(item: Observation) = repo.update(item)
}
