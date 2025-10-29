package com.mhike.app.features.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.usecase.AttachPhotoToHike
import com.mhike.app.domain.usecase.GetMediaForHike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMediaViewModel @Inject constructor(
    private val getMediaForHike: GetMediaForHike,
    private val attachPhotoToHike: AttachPhotoToHike
) : ViewModel() {

    fun media(hikeId: Long): StateFlow<List<com.mhike.app.domain.model.Media>> =
        getMediaForHike(hikeId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(hikeId: Long, picked: Uri, mime: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            attachPhotoToHike(hikeId, picked.toString(), mime)
            onDone()
        }
    }
}
