package com.mhike.app.domain.usecase

import com.mhike.app.domain.model.Media
import com.mhike.app.domain.repo.MediaRepository
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AttachPhotoToHike @Inject constructor(
    private val repo: MediaRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(hikeId: Long, uri: String, mime: String?): Long =
        repo.insert(
            Media(
                hikeId = hikeId,
                uri = uri,
                mimeType = mime,
                addedAt = Clock.System.now()
            )
        )
}
