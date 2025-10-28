package com.mhike.app.ui.hike.review

import com.mhike.app.ui.hike.form.HikeDraft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.usecase.CreateOrUpdateHike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class HikeReviewViewModel @Inject constructor(
    private val createOrUpdateHike: CreateOrUpdateHike
) : ViewModel() {

    private val drafts = mutableMapOf<String, HikeDraft>()

    fun putDraft(d: HikeDraft) {
        drafts[d.id] = d
    }

    fun getDraft(id: String): HikeDraft? = drafts[id]

    fun confirmSave(d: HikeDraft, onSaved: () -> Unit) {
        viewModelScope.launch {
            val hike = Hike(
                name = d.name,
                location = d.location,
                date = d.date ?: LocalDate(1970, 1, 1),
                parking = d.parking == true,
                lengthKm = d.lengthKm.toDoubleOrNull() ?: 0.0,
                difficulty = d.difficulty,
                description = d.description.ifBlank { null },
                terrain = d.terrain.ifBlank { null },
                expectedWeather = d.expectedWeather.ifBlank { null }
            )
            createOrUpdateHike(hike)
            onSaved()
        }
    }
}

