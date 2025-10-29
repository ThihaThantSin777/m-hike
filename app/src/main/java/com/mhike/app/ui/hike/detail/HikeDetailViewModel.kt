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

    private val hikeId: Long = checkNotNull(savedStateHandle.get<Long>("hikeId")) {
        "HikeDetailViewModel requires hikeId as a navigation argument"
    }

    init {
        loadHikeDetails()
        loadHikePhotos()
    }

    private fun loadHikeDetails() {
        viewModelScope.launch {
            try {
                getHikeById(hikeId).collectLatest { hike ->
                    _ui.value = if (hike != null) {
                        HikeDetailUiState.Ready(hike)
                    } else {
                        HikeDetailUiState.NotFound
                    }
                }
            } catch (e: Exception) {
                _ui.value = HikeDetailUiState.NotFound
            }
        }
    }

    private fun loadHikePhotos() {
        viewModelScope.launch {
            try {
                getMediaForHike(hikeId).collectLatest { mediaList ->
                    _photos.value = mediaList
                }
            } catch (e: Exception) {
                _photos.value = emptyList()
            }
        }
    }

    fun attachPhoto(uri: String, mimeType: String?) {
        viewModelScope.launch {
            try {
                attachPhotoToHike(hikeId, uri, mimeType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePhoto(photo: Media) {
        viewModelScope.launch {
            try {
                mediaRepository.delete(photo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}