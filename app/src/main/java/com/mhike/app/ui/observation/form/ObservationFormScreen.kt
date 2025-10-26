package com.mhike.app.ui.observation.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationFormScreen(
    hikeId: Long,
    obsId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: ObservationFormViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(obsId) {
        if (obsId != null) vm.loadForEdit(obsId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (obsId == null) "Add Observation" else "Edit Observation") },
                navigationIcon = { IconButton(onClick = onCancel) { Text("<") } }
            )
        }
    ) { pv ->
        Column(
            Modifier.fillMaxSize().padding(pv).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui.text,
                onValueChange = { vm.ui.value = ui.copy(text = it) },
                label = { Text("Observation *") }
            )
            OutlinedTextField(
                value = ui.comment,
                onValueChange = { vm.ui.value = ui.copy(comment = it) },
                label = { Text("Comment") }
            )

            Row {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { vm.save(hikeId, obsId, onSaved) }) { Text("Save") }
            }
        }
    }
}
