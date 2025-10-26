package com.mhike.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhike.app.domain.model.Hike

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenObservations: (Hike) -> Unit,
    vm: SearchViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()
    val results by vm.results.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Hikes") },
                navigationIcon = { IconButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { pv ->
        Column(
            Modifier.fillMaxSize().padding(pv).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = form.name,
                onValueChange = { vm.form.value = form.copy(name = it) },
                label = { Text("Name (prefix)") },
                singleLine = true
            )
            OutlinedTextField(
                value = form.location,
                onValueChange = { vm.form.value = form.copy(location = it) },
                label = { Text("Location (contains)") },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = form.minLen,
                    onValueChange = { vm.form.value = form.copy(minLen = it) },
                    label = { Text("Min km") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.maxLen,
                    onValueChange = { vm.form.value = form.copy(maxLen = it) },
                    label = { Text("Max km") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = form.startDate,
                    onValueChange = { vm.form.value = form.copy(startDate = it) },
                    label = { Text("Start date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = form.endDate,
                    onValueChange = { vm.form.value = form.copy(endDate = it) },
                    label = { Text("End date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("Results: ${results.size}")

            LazyColumn {
                items(results) { h ->
                    ListItem(
                        headlineContent = { Text(h.name) },
                        supportingContent = { Text("${h.location} • ${h.date} • ${h.lengthKm} km") },
                        modifier = Modifier.clickable { onOpenObservations(h) }
                    )
                    Divider()
                }
            }
        }
    }
}
