package com.mhike.app.ui.hike.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.usecase.DeleteHike
import com.mhike.app.domain.usecase.GetHikes
import com.mhike.app.domain.usecase.ResetDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikeListViewModel @Inject constructor(
    getHikes: GetHikes,
    private val deleteHike: DeleteHike,
    private val resetDatabase: ResetDatabase
) : ViewModel() {

    val hikes: StateFlow<List<Hike>> =
        getHikes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDelete(hike: Hike) {
        viewModelScope.launch { deleteHike(hike) }
    }

    fun onResetDatabase() {
        viewModelScope.launch { resetDatabase() }
    }
}
