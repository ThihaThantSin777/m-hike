package com.mhike.app.ui.hike.detail

import com.mhike.app.domain.model.Hike

sealed interface HikeDetailUiState {
    data object Loading : HikeDetailUiState
    data object NotFound : HikeDetailUiState
    data class Ready(val hike: Hike) : HikeDetailUiState
}