package com.mhike.app.ui.hike.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
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
        floatingActionButton = { FloatingActionButton(onClick = onAddClick) { Text("+") } }
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
                        supportingContent = { Text("${h.location} • ${h.date}") },
                        trailingContent = {
                            Text(
                                text = "Delete",
                                modifier = Modifier.clickable { onDelete(h) }
                            )
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
