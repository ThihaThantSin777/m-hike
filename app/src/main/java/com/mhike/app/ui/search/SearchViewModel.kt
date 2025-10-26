package com.mhike.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.model.Hike
import com.mhike.app.domain.usecase.SearchHikes
import com.mhike.app.domain.usecase.SearchParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import javax.inject.Inject

data class SearchForm(
    val name: String = "",
    val location: String = "",
    val minLen: String = "",
    val maxLen: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchHikes: SearchHikes
) : ViewModel() {

    val form = MutableStateFlow(SearchForm())

    private fun toParams(form: SearchForm): SearchParams {
        val minL = form.minLen.toDoubleOrNull()
        val maxL = form.maxLen.toDoubleOrNull()
        val start = form.startDate.trim().ifBlank { null }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val end   = form.endDate.trim().ifBlank { null }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val nameQ = form.name.trim().ifBlank { null }
        val locQ  = form.location.trim().ifBlank { null }
        return SearchParams(
            name = nameQ,
            location = locQ,
            minLen = minL,
            maxLen = maxL,
            startDate = start,
            endDate = end
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val results: StateFlow<List<Hike>> =
        form
            .combine(MutableStateFlow(Unit)) { f, _ -> f }
            .flatMapLatest { f -> searchHikes(toParams(f)) }
            .stateIn(
                scope = viewModelScope,   // ✅ now resolves correctly
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}
