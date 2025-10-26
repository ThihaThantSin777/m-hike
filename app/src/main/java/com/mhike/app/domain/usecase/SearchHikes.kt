package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.repo.HikeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

data class SearchParams(
    val name: String? = null,
    val location: String? = null,
    val minLen: Double? = null,
    val maxLen: Double? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

class SearchHikes @Inject constructor(
    private val repo: HikeRepository
) {
    operator fun invoke(p: SearchParams): Flow<List<Hike>> =
        repo.search(p.name, p.location, p.minLen, p.maxLen, p.startDate, p.endDate)
}
