package com.mhike.app.ui.observation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.usecase.DeleteObservation
import com.mhike.app.domain.usecase.GetObservationsForHike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObservationListViewModel @Inject constructor(
    private val getObservationsForHike: GetObservationsForHike,
    private val deleteObservation: DeleteObservation
) : ViewModel() {

    fun state(hikeId: Long): StateFlow<List<Observation>> =
        getObservationsForHike(hikeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDelete(item: Observation) {
        viewModelScope.launch { deleteObservation(item) }
    }
}
