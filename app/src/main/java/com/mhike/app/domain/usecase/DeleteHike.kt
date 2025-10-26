package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.repo.HikeRepository
import javax.inject.Inject

class DeleteHike @Inject constructor(
    private val repo: HikeRepository
) {
    suspend operator fun invoke(hike: Hike) = repo.delete(hike)
}
