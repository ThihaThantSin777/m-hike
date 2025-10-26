package com.mhike.app.features.media

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.mhike.app.util.MediaStoreUtils

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaSheet(
    hikeId: Long,
    onDismiss: () -> Unit,
    vm: AddMediaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val media by remember(hikeId) { vm.media(hikeId) }.collectAsState()

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val pickPhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            vm.save(hikeId, uri, "image/*") {  }
        }
    }

    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { vm.save(hikeId, it, "image/jpeg") }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add photos", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Pick from gallery") }
                val requestCamera = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) {
                        val uri = MediaStoreUtils.createImageUri(context)
                            ?: return@rememberLauncherForActivityResult
                        pendingCameraUri = uri
                        takePicture.launch(uri)
                    }
                }

                Button(onClick = {
                    val uri = MediaStoreUtils.createImageUri(context) ?: return@Button
                    pendingCameraUri = uri
                    takePicture.launch(uri)
                }) { Text("Take photo") }
            }

            Divider(Modifier.padding(vertical = 8.dp))
            Text("Attached", style = MaterialTheme.typography.titleSmall)

            if (media.isEmpty()) {
                Text("No photos yet.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    items(media) { m ->
                        Image(
                            painter = rememberAsyncImagePainter(m.uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
