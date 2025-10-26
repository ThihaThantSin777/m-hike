package com.mhike.app.ui.hike.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeFormScreen(
    onReview: (draftId: String) -> Unit,
    onBack: () -> Unit,
    vm: HikeFormViewModel = hiltViewModel()
) {
    var formError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("New Hike") }) }
    ) { pv ->
        Column(
            Modifier.fillMaxSize().padding(pv).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = vm.draft.name,
                onValueChange = { value -> vm.update { it.copy(name = value) } },
                label = { Text("Name *") },
                isError = false
            )

            OutlinedTextField(
                value = vm.draft.location,
                onValueChange = { value -> vm.update { it.copy(location = value) } },
                label = { Text("Location *") },
                isError = false
            )

            OutlinedTextField(
                value = vm.draft.date?.toString() ?: "",
                onValueChange = { s ->
                    val parsed = runCatching { LocalDate.parse(s) }.getOrNull()
                    vm.update { it.copy(date = parsed) }
                },
                label = { Text("Date (YYYY-MM-DD) *") },
                isError = false
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = vm.draft.parking == true,
                    onClick = { vm.update { it.copy(parking = true) } },
                    label = { Text("Parking Yes") }
                )
                FilterChip(
                    selected = vm.draft.parking == false,
                    onClick = { vm.update { it.copy(parking = false) } },
                    label = { Text("No") }
                )
            }

            OutlinedTextField(
                value = vm.draft.lengthKm,
                onValueChange = { value -> vm.update { it.copy(lengthKm = value) } },
                label = { Text("Length (km) *") },
                isError = false
            )

            OutlinedTextField(
                value = vm.draft.difficulty,
                onValueChange = { value -> vm.update { it.copy(difficulty = value) } },
                label = { Text("Difficulty * (Easy/Moderate/Hard)") },
                isError = false
            )

            OutlinedTextField(
                value = vm.draft.description,
                onValueChange = { value -> vm.update { it.copy(description = value) } },
                label = { Text("Description") }
            )
            OutlinedTextField(
                value = vm.draft.terrain,
                onValueChange = { value -> vm.update { it.copy(terrain = value) } },
                label = { Text("Terrain") }
            )
            OutlinedTextField(
                value = vm.draft.expectedWeather,
                onValueChange = { value -> vm.update { it.copy(expectedWeather = value) } },
                label = { Text("Expected Weather") }
            )

            formError?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(8.dp))
            Row {
                TextButton(onClick = onBack) { Text("Cancel") }
                Spacer(Modifier.height(0.dp))
                Button(onClick = {
                    val vr = vm.validate()
                    if (!vr.isValid) {
                        formError = vr.errorMessage ?: "Please fix the form."
                    } else {
                        formError = null
                        onReview(vm.draft.id)
                    }
                }) { Text("Review") }
            }
        }
    }
}
