package com.mhike.app.domain.usecase

import com.mhike.app.domain.repo.HikeRepository
import javax.inject.Inject

class ResetDatabase @Inject constructor(
    private val repo: HikeRepository
) {
    suspend operator fun invoke() = repo.reset()
}
