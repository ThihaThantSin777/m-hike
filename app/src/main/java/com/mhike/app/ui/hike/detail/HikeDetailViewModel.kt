package com.mhike.app.ui.hike.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.usecase.GetHikeById
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class HikeDetailViewModel @Inject constructor(
    private val getHikeById: GetHikeById,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _ui = MutableStateFlow<HikeDetailUiState>(HikeDetailUiState.Loading)
    val ui: StateFlow<HikeDetailUiState> = _ui

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
    }
}
