package com.mhike.app.ui.observation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ObservationListScreen(
    hikeId: Long,
    hikeName: String,
    onAdd: () -> Unit,
    onEdit: (obsId: Long) -> Unit,
    onBack: () -> Unit,
    vm: ObservationListViewModel = hiltViewModel()
) {
    val observations by remember(hikeId) { vm.state(hikeId) }.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Observations • $hikeName") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("<") }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { pv ->
        if (observations.isEmpty()) {
            Box(Modifier
                .fillMaxSize()
                .padding(pv)) {
                Text("No observations yet. Tap + to add.", Modifier.padding(24.dp))
            }
        } else {
            LazyColumn(Modifier
                .fillMaxSize()
                .padding(pv)) {
                items(observations) { o ->
                    ListItem(
                        headlineContent = { Text(o.text) },
                        supportingContent = {
                            Text(o.at.toString() + (o.comment?.let { " • $it" } ?: ""))
                        },
                        trailingContent = {
                            Row {
                                TextButton(onClick = { onEdit(o.id) }) { Text("Edit") }
                                TextButton(onClick = { /* delete */ vm.onDelete(o) }) { Text("Delete") }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
