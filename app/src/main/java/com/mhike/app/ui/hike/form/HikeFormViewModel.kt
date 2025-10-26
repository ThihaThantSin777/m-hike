package com.mhike.app.ui.hike.form

import androidx.lifecycle.ViewModel
import com.mhike.app.domain.model.Hike
import com.mhike.app.util.ValidationResult
import com.mhike.app.util.validateHike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.LocalDate
import java.util.UUID
import javax.inject.Inject

data class HikeDraft(
    val id: String = UUID.randomUUID().toString(),
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

@HiltViewModel
class HikeFormViewModel @Inject constructor() : ViewModel() {

    var draft = HikeDraft()
        private set

    fun update(transform: (HikeDraft) -> HikeDraft) {
        draft = transform(draft)
    }

    fun validate(): ValidationResult {
        val hike = Hike(
            id = 0L,
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
}
