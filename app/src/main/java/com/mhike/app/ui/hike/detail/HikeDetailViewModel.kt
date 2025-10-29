package com.mhike.app.ui.hike.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Media
import com.mhike.app.domain.usecase.AttachPhotoToHike
import com.mhike.app.domain.usecase.GetHikeById
import com.mhike.app.domain.usecase.GetMediaForHike
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mhike.app.domain.repo.MediaRepository


@HiltViewModel
class HikeDetailViewModel @Inject constructor(
    private val getHikeById: GetHikeById,
    private val getMediaForHike: GetMediaForHike,
    private val attachPhotoToHike: AttachPhotoToHike,
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _ui = MutableStateFlow<HikeDetailUiState>(HikeDetailUiState.Loading)
    val ui: StateFlow<HikeDetailUiState> = _ui

    private val _photos = MutableStateFlow<List<Media>>(emptyList())
    val photos: StateFlow<List<Media>> = _photos

    private val hikeId: Long = run {
        val fromLong = savedStateHandle.get<Long>("hikeId")
        requireNotNull(fromLong ?: -1) { "Missing nav argument: hikeId" }
    }

    init {
        viewModelScope.launch {
            getHikeById(hikeId).collectLatest { hike ->
                _ui.value = if (hike != null) {
                    HikeDetailUiState.Ready(hike)
                } else {
                    HikeDetailUiState.NotFound
                }
            }
        }

        viewModelScope.launch {
            getMediaForHike(hikeId).collectLatest { mediaList ->
                _photos.value = mediaList
            }
        }
    }

    fun attachPhoto(uri: String, mimeType: String?) {
        viewModelScope.launch {
            attachPhotoToHike(hikeId, uri, mimeType)
        }
    }

    fun deletePhoto(photo: Media) {
        viewModelScope.launch {
            mediaRepository.delete(photo)
        }
    }
}