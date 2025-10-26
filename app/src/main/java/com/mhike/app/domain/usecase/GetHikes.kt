package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.repo.HikeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHikes @Inject constructor(
    private val repo: HikeRepository
) {
    operator fun invoke(): Flow<List<Hike>> = repo.observeAll()
}
