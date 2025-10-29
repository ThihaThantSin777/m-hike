package com.mhike.app.ui.hike.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.usecase.CreateOrUpdateHike
import com.mhike.app.domain.usecase.GetHikeById
import com.mhike.app.util.ValidationResult
import com.mhike.app.util.validateHike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class HikeFormViewModel @Inject constructor(
    private val createOrUpdateHike: CreateOrUpdateHike,
    private val getHikeById: GetHikeById
) : ViewModel() {

    var draft by mutableStateOf(HikeDraft())
        private set

    fun update(transform: (HikeDraft) -> HikeDraft) {
        draft = transform(draft)
    }

    fun loadForEdit(hikeId: Long) {
        viewModelScope.launch {
            val hike = getHikeById(hikeId).first() ?: return@launch
            draft = HikeDraft(
                id = hike.id.toString(),
                name = hike.name,
                location = hike.location,
                date = hike.date,
                parking = hike.parking,
                lengthKm = hike.lengthKm.toString(),
                difficulty = hike.difficulty,
                description = hike.description ?: "",
                terrain = hike.terrain ?: "",
                expectedWeather = hike.expectedWeather ?: ""
            )
        }
    }

    fun validate(): ValidationResult {
        val hike = Hike(
            id = draft.id.toLongOrNull() ?: 0L,
            name = draft.name.trim(),
            location = draft.location.trim(),
            date = draft.date,
            parking = draft.parking ?: false,
            lengthKm = draft.lengthKm.toDoubleOrNull() ?: 0.0,
            difficulty = draft.difficulty.trim(),
            description = draft.description.trim(),
            terrain = draft.terrain.trim(),
            expectedWeather = draft.expectedWeather.trim()
        )
        return validateHike(hike)
    }

    fun saveHike() {
        viewModelScope.launch {
            val hike = Hike(
                id = draft.id.toLongOrNull() ?: 0L,
                name = draft.name.trim(),
                location = draft.location.trim(),
                date = draft.date ?: LocalDate(1970, 1, 1),
                parking = draft.parking == true,
                lengthKm = draft.lengthKm.toDoubleOrNull() ?: 0.0,
                difficulty = draft.difficulty.trim(),
                description = draft.description.trim().ifBlank { null },
                terrain = draft.terrain.trim().ifBlank { null },
                expectedWeather = draft.expectedWeather.trim().ifBlank { null }
            )
            createOrUpdateHike(hike)
        }
    }
}

data class HikeDraft(
    val id: String = "0",
    val name: String = "",
    val location: String = "",
    val date: LocalDate? = null,
    val parking: Boolean? = null,
    val lengthKm: String = "",
    val difficulty: String = "",
    val description: String = "",
    val terrain: String = "",
    val expectedWeather: String = ""
)