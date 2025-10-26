package com.mhike.app.ui.hike.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.usecase.DeleteHike
import com.mhike.app.domain.usecase.GetHikeById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.datetime.LocalDate

@HiltViewModel
class HikeDetailViewModel @Inject constructor(
    private val getHikeById: GetHikeById,
    private val deleteHike: DeleteHike
) : ViewModel() {


    lateinit var hike: StateFlow<Hike>

    fun setHikeId(id: Long) {
        if (!::hike.isInitialized) {
            hike = getHikeById(id)
                .filterNotNull()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = Hike(
                        id = id,
                        name = "",
                        location = "",
                        date = LocalDate(1970, 1, 1),
                        parking = false,
                        lengthKm = 0.0,
                        difficulty = ""
                    )
                )
        }
    }

    suspend fun delete(current: Hike) = deleteHike(current)
}
