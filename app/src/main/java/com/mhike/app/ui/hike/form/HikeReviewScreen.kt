package com.mhike.app.ui.hike.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    fun putDraft(d: HikeDraft) { drafts[d.id] = d }
    fun getDraft(id: String): HikeDraft? = drafts[id]

    fun confirmSave(d: HikeDraft, onSaved: () -> Unit) {
        viewModelScope.launch {
            val hike = Hike(
                name = d.name,
                location = d.location,
                date = d.date ?: LocalDate(1970,1,1),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeReviewScreen(
    draftId: String,
    onConfirmSaved: () -> Unit,
    onEdit: () -> Unit,
    formVm: HikeFormViewModel = hiltViewModel(),
    vm: HikeReviewViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.putDraft(formVm.draft) }
    val d = vm.getDraft(draftId) ?: formVm.draft

    Scaffold(
        topBar = { TopAppBar(title = { Text("Review Hike") }) }
    ) { pv ->
        Column(
            Modifier.fillMaxSize().padding(pv).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Name: ${d.name}")
                    Text("Location: ${d.location}")
                    Text("Date: ${d.date}")
                    Text("Parking: ${if (d.parking == true) "Yes" else "No"}")
                    Text("Length: ${d.lengthKm} km")
                    Text("Difficulty: ${d.difficulty}")
                    if (d.description.isNotBlank()) Text("Description: ${d.description}")
                    if (d.terrain.isNotBlank()) Text("Terrain: ${d.terrain}")
                    if (d.expectedWeather.isNotBlank()) Text("Expected Weather: ${d.expectedWeather}")
                }
            }

            Row {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                Spacer(Modifier.height(0.dp))
                Button(onClick = { vm.confirmSave(d) { onConfirmSaved() } }) { Text("Confirm & Save") }
            }
        }
    }
}
