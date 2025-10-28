package com.mhike.app.ui.hike.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mhike.app.domain.model.Hike
import com.mhike.app.ui.components.ConfirmDialog
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeListScreen(
    hikesFlow: StateFlow<List<Hike>>,
    onAddClick: () -> Unit,
    onDelete: (Hike) -> Unit,
    onResetDatabase: () -> Unit,
    onOpenObservations: (Hike) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDetail: (Hike) -> Unit
) {
    val hikes by hikesFlow.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M-Hike") },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Reset database") },
                            onClick = {
                                menuOpen = false
                                showConfirm = true
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { pv ->
        if (hikes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pv)) {
                Text("No hikes yet. Tap + to add.", Modifier.padding(24.dp))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pv)) {
                items(hikes) { h ->
                    ListItem(
                        headlineContent = { Text(h.name) },
                        supportingContent = {
                            Text("${h.location} • ${h.date}")
                        },
                        trailingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Obs",
                                    modifier = Modifier.clickable { onOpenObservations(h) },
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Delete",
                                    modifier = Modifier.clickable { onDelete(h) },
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier
                            .clickable { onOpenDetail(h) }
                            .padding(horizontal = 8.dp)
                    )
                    Divider()
                }
            }
        }
    }

    if (showConfirm) {
        ConfirmDialog(
            title = "Reset database?",
            message = "This will delete all hikes and observations. This action cannot be undone.",
            confirmText = "Reset",
            onConfirm = {
                showConfirm = false
                onResetDatabase()
            },
            onDismiss = { showConfirm = false }
        )
    }
}
