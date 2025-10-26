package com.mhike.app.ui.hike.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhike.app.domain.model.Hike
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import com.mhike.app.features.media.AddMediaSheet
import com.mhike.app.features.media.AddMediaViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.setValue
import coil.compose.rememberAsyncImagePainter
import com.mhike.app.domain.model.Media
import com.mhike.app.domain.usecase.GetMediaForHike


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeDetailScreen(
    hikeId: Long,
    onBack: () -> Unit,
    onOpenObservations: (hikeId: Long, hikeName: String) -> Unit,
    onAddObservation: (hikeId: Long) -> Unit,
    vm: HikeDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(hikeId) { vm.setHikeId(hikeId) }
    val hike: Hike by vm.hike.collectAsState()

    val scope: CoroutineScope = rememberCoroutineScope()
    val (showDelete, setShowDelete) = remember { mutableStateOf(false) }

    val mediaVm: AddMediaViewModel = hiltViewModel()
    val media by remember(hikeId) { mediaVm.media(hikeId) }.collectAsState()
    var showAddMedia by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hike.name.ifBlank { "Hike Detail" }) },
                navigationIcon = { IconButton(onClick = onBack) { Text("<") } },
                actions = { TextButton(onClick = { setShowDelete(true) }) { Text("Delete") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMedia = true },
            ) {
                Text("Add Observation")
            }
        }
    ) { pv ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailRow("Location", hike.location)
            DetailRow("Date", hike.date.toString())
            DetailRow("Parking", if (hike.parking) "Yes" else "No")
            DetailRow("Length", "${hike.lengthKm} km")
            DetailRow("Difficulty", hike.difficulty)
            hike.description?.takeIf { it.isNotBlank() }?.let { DetailRow("Description", it) }
            hike.terrain?.takeIf { it.isNotBlank() }?.let { DetailRow("Terrain", it) }
            hike.expectedWeather?.takeIf { it.isNotBlank() }
                ?.let { DetailRow("Expected Weather", it) }
            if (media.isNotEmpty()) {
                Text("Photos", style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp)
                ) {
                    items(media) { m ->
                        Image(
                            painter = rememberAsyncImagePainter(m.uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }


            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                onOpenObservations(hikeId, hike.name.ifBlank { "Hike $hikeId" })
            }) {
                Text("View Observations")
            }
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { setShowDelete(false) },
            title = { Text("Delete hike?") },
            text = { Text("This will remove the hike and its observations.") },
            confirmButton = {
                TextButton(onClick = {
                    setShowDelete(false)
                    scope.launch { vm.delete(hike); onBack() }
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { setShowDelete(false) }) { Text("Cancel") } }
        )
    }

    if (showAddMedia) {
        AddMediaSheet(
            hikeId = hikeId,
            onDismiss = { showAddMedia = false }
        )
    }

}

@Composable
private fun DetailRow(label: String, value: String) {
    ElevatedCard {
        Column(Modifier
            .fillMaxSize()
            .padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
