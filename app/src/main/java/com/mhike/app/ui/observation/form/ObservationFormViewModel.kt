package com.mhike.app.ui.observation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Observation
import com.mhike.app.domain.usecase.AddObservation
import com.mhike.app.domain.usecase.GetObservationById
import com.mhike.app.domain.usecase.UpdateObservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ObservationFormState @OptIn(ExperimentalTime::class) constructor(
    val text: String = "",
    val comment: String = "",
    val photoUris: List<String> = emptyList(),
    val observationTime: Instant = Clock.System.now()
)

@HiltViewModel
class ObservationFormViewModel @Inject constructor(
    private val addObservation: AddObservation,
    private val updateObservation: UpdateObservation,
    private val getObservationById: GetObservationById
) : ViewModel() {

    val ui = MutableStateFlow(ObservationFormState())

    fun loadForEdit(obsId: Long) {
        viewModelScope.launch {
            val existing = getObservationById(obsId).first() ?: return@launch
            ui.value = ObservationFormState(
                text = existing.text,
                comment = existing.comment ?: "",
                photoUris = existing.photoUris ?: emptyList(),
                observationTime = existing.at
            )
        }
    }

    fun addPhoto(uri: String) {
        ui.value = ui.value.copy(
            photoUris = ui.value.photoUris + uri
        )
    }

    fun removePhoto(uri: String) {
        ui.value = ui.value.copy(
            photoUris = ui.value.photoUris.filter { it != uri }
        )
    }

    @OptIn(ExperimentalTime::class)
    fun updateTime(newTime: Instant) {
        ui.value = ui.value.copy(observationTime = newTime)
    }

    fun save(hikeId: Long, obsId: Long?, onDone: () -> Unit) {
        viewModelScope.launch {
            val state = ui.value
            if (state.text.isBlank()) return@launch

            if (obsId == null) {
                addObservation(
                    Observation(
                        hikeId = hikeId,
                        text = state.text,
                        comment = state.comment.ifBlank { null },
                        photoUris = state.photoUris.ifEmpty { null },
                        at = state.observationTime
                    )
                )
            } else {
                updateObservation(
                    Observation(
                        id = obsId,
                        hikeId = hikeId,
                        text = state.text,
                        comment = state.comment.ifBlank { null },
                        photoUris = state.photoUris.ifEmpty { null },
                        at = state.observationTime
                    )
                )
            }
            onDone()
        }
    }
}