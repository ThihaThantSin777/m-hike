package com.mhike.app.domain.usecase

import com.mhike.app.data.local.dao.HikeDao
import com.mhike.app.data.local.seed.HikeSeed
import javax.inject.Inject

class SeedHikesIfEmpty @Inject constructor(
    private val hikeDao: HikeDao
) {
    suspend operator fun invoke() {
        if (hikeDao.count() == 0) {
            hikeDao.insertAll(HikeSeed.sample())
        }
    }
}
