package com.mhike.app.ui.hike.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeDetailScreen(
    onBack: () -> Unit,
    vm: HikeDetailViewModel = hiltViewModel()
) {
    val state by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hike Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { pv ->
        when (val s = state) {
            is HikeDetailUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is HikeDetailUiState.NotFound -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv),
                contentAlignment = Alignment.Center
            ) { Text("Hike not found") }

            is HikeDetailUiState.Ready -> {
                val hike = s.hike
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(hike.name, style = MaterialTheme.typography.headlineSmall)
                    Text(hike.location, style = MaterialTheme.typography.titleMedium)
                    hike.date?.let { Text("Date: $it") }
                    Text("Length: ${hike.lengthKm} km")
                    Text("Difficulty: ${hike.difficulty}")
                    Text("Parking: ${if (hike.parking) "Yes" else "No"}")
                    if (hike.terrain?.isNotBlank() ?: false) Text("Terrain: ${hike.terrain}")
                    if (hike.expectedWeather?.isNotBlank()
                            ?: false
                    ) Text("Expected weather: ${hike.expectedWeather}")
                    if (hike.description?.isNotBlank() ?: false) {
                        Spacer(Modifier.height(8.dp))
                        Text(hike.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
