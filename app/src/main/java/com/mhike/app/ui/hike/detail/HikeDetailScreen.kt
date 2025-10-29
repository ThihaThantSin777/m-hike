package com.mhike.app.ui.hike.detail

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mhike.app.domain.model.Media
import com.mhike.app.domain.model.WeatherInfo
import com.mhike.app.ui.weather.HikeWeatherViewModel
import com.mhike.app.ui.weather.WeatherUiState
import com.mhike.app.util.MediaStoreUtils
import androidx.core.net.toUri


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HikeDetailScreen(
    onBack: () -> Unit,
    vm: HikeDetailViewModel = hiltViewModel(),
    weatherVm: HikeWeatherViewModel = hiltViewModel()
) {
    val state by vm.ui.collectAsState()
    val photos by vm.photos.collectAsState()
    val weatherState by weatherVm.state.collectAsState()
    val context = LocalContext.current

    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<Media?>(null) }
    var showDeletePhotoDialog by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<Media?>(null) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val photoPermission = if (Build.VERSION.SDK_INT >= 33) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && capturedImageUri != null) {
            vm.attachPhoto(capturedImageUri.toString(), "image/jpeg")
            capturedImageUri = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.attachPhoto(it.toString(), context.contentResolver.getType(it))
        }
    }

    LaunchedEffect(state) {
        if (state is HikeDetailUiState.Ready) {
            val hike = (state as HikeDetailUiState.Ready).hike
            weatherVm.loadByCity(hike.location)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Hike Details",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        when (val s = state) {
            is HikeDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1565C0),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Loading hike details...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is HikeDetailUiState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Text(
                            text = "Hike Not Found",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "The hike you're looking for doesn't exist or has been deleted.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1565C0)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Go Back")
                        }
                    }
                }
            }

            is HikeDetailUiState.Ready -> {
                val hike = s.hike
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1565C0).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Terrain,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = hike.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF26A69A)
                                    )
                                    Text(
                                        text = hike.location,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Weather Card
                    AnimatedWeatherCard(weatherState = weatherState, onRefresh = {
                        weatherVm.refresh();
                    })

                    // Photos Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Photos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = photos.size.toString(),
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 1.dp)

                            if (photos.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                        Text(
                                            text = "No photos yet",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        TextButton(
                                            onClick = { showPhotoDialog = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("Add photos")
                                        }
                                    }
                                }
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(photos) { photo ->
                                        PhotoThumbnail(
                                            photo = photo,
                                            onClick = { selectedPhoto = photo },
                                            onLongClick = {
                                                photoToDelete = photo
                                                showDeletePhotoDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Key Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Key Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(thickness = 1.dp)

                            hike.date?.let { date ->
                                DetailItem(
                                    icon = Icons.Default.DateRange,
                                    label = "Date",
                                    value = date.toString(),
                                    iconTint = Color(0xFF1565C0)
                                )
                            }

                            DetailItem(
                                icon = Icons.Default.Straighten,
                                label = "Distance",
                                value = "${hike.lengthKm} km",
                                iconTint = Color(0xFFFF9800)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF7E57C2).copy(alpha = 0.1f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color(0xFF7E57C2)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Difficulty Level",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (hike.difficulty.lowercase()) {
                                            "easy" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                            "moderate" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                            "hard" -> Color(0xFFF44336).copy(alpha = 0.15f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ) {
                                        Text(
                                            text = hike.difficulty,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = when (hike.difficulty.lowercase()) {
                                                "easy" -> Color(0xFF2E7D32)
                                                "moderate" -> Color(0xFFE65100)
                                                "hard" -> Color(0xFFC62828)
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (hike.parking)
                                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                                    else
                                        Color(0xFFF44336).copy(alpha = 0.1f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (hike.parking)
                                                Icons.Default.LocalParking
                                            else
                                                Icons.Default.RemoveCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = if (hike.parking)
                                                Color(0xFF4CAF50)
                                            else
                                                Color(0xFFF44336)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Parking Available",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = if (hike.parking) "Yes" else "No",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hike.parking)
                                            Color(0xFF4CAF50)
                                        else
                                            Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                    }

                    // Additional Details Card
                    if ((hike.description?.isNotBlank() == true) ||
                        (hike.terrain?.isNotBlank() == true) ||
                        (hike.expectedWeather?.isNotBlank() == true)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Additional Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(thickness = 1.dp)

                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    if (hike.description?.isNotBlank() == true) {
                                        DetailTextBlock(
                                            icon = Icons.AutoMirrored.Filled.Notes,
                                            label = "Description",
                                            value = hike.description,
                                            iconTint = Color(0xFF1565C0)
                                        )
                                    }

                                    if (hike.terrain?.isNotBlank() == true) {
                                        DetailTextBlock(
                                            icon = Icons.Default.Landscape,
                                            label = "Terrain Type",
                                            value = hike.terrain,
                                            iconTint = Color(0xFF8D6E63)
                                        )
                                    }

                                    if (hike.expectedWeather?.isNotBlank() == true) {
                                        DetailTextBlock(
                                            icon = Icons.Default.WbSunny,
                                            label = "Expected Weather",
                                            value = hike.expectedWeather,
                                            iconTint = Color(0xFFFFA726)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Photo Dialog
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            },
            title = {
                Text(
                    text = "Add Photo",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Choose how you want to add a photo to this hike",
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            showPhotoDialog = false
                            if (cameraPermission.status.isGranted) {
                                capturedImageUri =
                                    MediaStoreUtils.createImageUri(context, "mhike_hike")
                                capturedImageUri?.let { cameraLauncher.launch(it) }
                            } else {
                                cameraPermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Take Photo")
                    }

                    OutlinedButton(
                        onClick = {
                            showPhotoDialog = false
                            if (photoPermission.status.isGranted) {
                                galleryLauncher.launch("image/*")
                            } else {
                                photoPermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Choose from Gallery")
                    }

                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { showPhotoDialog = false }) {
                        Text("Cancel")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Full Photo Dialog
    if (selectedPhoto != null) {
        Dialog(
            onDismissRequest = { selectedPhoto = null }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp)
                    ) {
                        AsyncImage(
                            model = selectedPhoto!!.uri.toUri(),
                            contentDescription = "Full photo",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )

                        IconButton(
                            onClick = { selectedPhoto = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.5f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoToDelete = selectedPhoto
                                selectedPhoto = null
                                showDeletePhotoDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    // Delete photo confirmation
    if (showDeletePhotoDialog && photoToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeletePhotoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Delete Photo?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete this photo? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        photoToDelete?.let { vm.deletePhoto(it) }
                        photoToDelete = null
                        showDeletePhotoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    photoToDelete = null
                    showDeletePhotoDialog = false
                }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}


@Composable
fun WeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = iconTint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun getWeatherIcon(description: String): ImageVector {
    return when {
        description.contains("clear", ignoreCase = true) -> Icons.Default.WbSunny
        description.contains("cloud", ignoreCase = true) -> Icons.Default.Cloud
        description.contains("rain", ignoreCase = true) -> Icons.Default.Umbrella
        description.contains("snow", ignoreCase = true) -> Icons.Default.AcUnit
        description.contains("thunder", ignoreCase = true) -> Icons.Default.Bolt
        description.contains("mist", ignoreCase = true) ||
                description.contains("fog", ignoreCase = true) -> Icons.Default.Cloud

        else -> Icons.Default.WbCloudy
    }
}

@Composable
fun PhotoThumbnail(
    photo: Media,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.uri.toUri(),
                contentDescription = "Hike photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onLongClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = iconTint.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DetailTextBlock(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


@Composable
fun AnimatedWeatherCard(
    weatherState: WeatherUiState,
    onRefresh: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF64B5F6).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Current Weather",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Refresh button
                IconButton(
                    onClick = onRefresh,
                    enabled = weatherState !is WeatherUiState.Loading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh weather",
                        tint = if (weatherState is WeatherUiState.Loading)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        else
                            Color(0xFF1976D2)
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp)

            when (weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF1976D2),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Loading weather...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    AnimatedWeatherContent(weather = weatherState.data)
                }

                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Unable to load weather",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = weatherState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                is WeatherUiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Weather information will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AnimatedWeatherContent(weather: WeatherInfo) {
    val description = (weather.description ?: weather.summary ?: "").lowercase()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Animated weather background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Weather animation based on condition
            when {
                description.contains("rain") -> RainAnimation()
                description.contains("snow") -> SnowAnimation()
                description.contains("cloud") -> CloudAnimation()
                description.contains("clear") || description.contains("sun") -> SunAnimation()
                description.contains("thunder") || description.contains("storm") -> ThunderAnimation()
                else -> CloudAnimation()
            }

            // Main temperature display overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", weather.tempC),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = getWeatherIcon(description),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1976D2)
                        )
                        Text(
                            text = (weather.description ?: weather.summary ?: "N/A")
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Animated weather icon
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF64B5F6).copy(alpha = 0.3f),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AnimatedWeatherIcon(description = description)
                    }
                }
            }
        }

        // Weather details grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Feels Like
            weather.feelsLikeC?.let { feelsLike ->
                WeatherDetailCard(
                    icon = Icons.Default.Thermostat,
                    label = "Feels Like",
                    value = String.format("%.1f°C", feelsLike),
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFFFF7043)
                )
            }

            // Humidity
            weather.humidityPercent?.let { humidity ->
                WeatherDetailCard(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "$humidity%",
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFF42A5F5)
                )
            }
        }

        // Second row of details
        if (weather.windSpeedMs != null || weather.tempMinC != null || weather.tempMaxC != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wind Speed
                weather.windSpeedMs?.let { windSpeed ->
                    WeatherDetailCard(
                        icon = Icons.Default.Air,
                        label = "Wind Speed",
                        value = String.format("%.1f m/s", windSpeed),
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF66BB6A)
                    )
                }

                // Temperature Range (Min/Max)
                if (weather.tempMinC != null && weather.tempMaxC != null) {
                    WeatherDetailCard(
                        icon = Icons.Default.Thermostat,
                        label = "Min / Max",
                        value = String.format("%.0f° / %.0f°", weather.tempMinC, weather.tempMaxC),
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF9575CD)
                    )
                }
            }
        }

        // Location info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = buildString {
                    append(weather.placeName)
                    weather.countryCode?.let { append(", $it") }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedWeatherIcon(description: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_icon")

    when {
        description.contains("clear") || description.contains("sun") -> {
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "sun_rotation"
            )

            Canvas(modifier = Modifier.size(60.dp)) {
                rotate(rotation) {
                    // Draw sun rays
                    for (i in 0 until 8) {
                        val angle = (i * 45f) * (Math.PI / 180f).toFloat()
                        val startX = center.x + cos(angle) * 25.dp.toPx()
                        val startY = center.y + sin(angle) * 25.dp.toPx()
                        val endX = center.x + cos(angle) * 35.dp.toPx()
                        val endY = center.y + sin(angle) * 35.dp.toPx()

                        drawLine(
                            color = Color(0xFFFFA726),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 4.dp.toPx()
                        )
                    }
                }
                // Draw sun center
                drawCircle(
                    color = Color(0xFFFFA726),
                    radius = 20.dp.toPx(),
                    center = center
                )
            }
        }

        else -> {
            Icon(
                imageVector = getWeatherIcon(description),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF1976D2)
            )
        }
    }
}

@Composable
fun RainAnimation() {
    val rainDrops = remember {
        List(30) {
            RainDrop(
                x = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.5f,
                length = Random.nextFloat() * 20f + 15f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        rainDrops.forEach { drop ->
            val startY = -50f + (size.height + 50f) * ((animationProgress * drop.speed) % 1f)
            val x = drop.x * size.width

            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF64B5F6).copy(alpha = 0.6f),
                        Color(0xFF64B5F6).copy(alpha = 0.2f)
                    )
                ),
                start = Offset(x, startY),
                end = Offset(x, startY + drop.length),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun SnowAnimation() {
    val snowFlakes = remember {
        List(25) {
            SnowFlake(
                x = Random.nextFloat(),
                speed = Random.nextFloat() * 0.3f + 0.2f,
                size = Random.nextFloat() * 6f + 3f,
                swing = Random.nextFloat() * 0.02f + 0.01f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        snowFlakes.forEach { flake ->
            val progress = (animationProgress * flake.speed) % 1f
            val y = -20f + (size.height + 20f) * progress
            val x = (flake.x + sin(progress * 10f) * flake.swing) * size.width

            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = flake.size.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun CloudAnimation() {
    val clouds = remember {
        listOf(
            CloudData(y = 0.3f, speed = 0.3f, size = 80f),
            CloudData(y = 0.5f, speed = 0.4f, size = 100f),
            CloudData(y = 0.7f, speed = 0.25f, size = 70f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "clouds")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        clouds.forEach { cloud ->
            val progress = (animationProgress * cloud.speed) % 1f
            val x = -cloud.size - 50f + (size.width + cloud.size + 100f) * progress
            val y = size.height * cloud.y

            // Draw cloud shape
            drawCircle(
                color = Color(0xFFB0BEC5).copy(alpha = 0.5f),
                radius = cloud.size.dp.toPx() * 0.5f,
                center = Offset(x, y)
            )
            drawCircle(
                color = Color(0xFFB0BEC5).copy(alpha = 0.5f),
                radius = cloud.size.dp.toPx() * 0.6f,
                center = Offset(x + cloud.size.dp.toPx() * 0.4f, y - cloud.size.dp.toPx() * 0.2f)
            )
            drawCircle(
                color = Color(0xFFB0BEC5).copy(alpha = 0.5f),
                radius = cloud.size.dp.toPx() * 0.55f,
                center = Offset(x + cloud.size.dp.toPx() * 0.8f, y)
            )
        }
    }
}

@Composable
fun SunAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_scale"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * 0.75f
        val centerY = size.height * 0.3f

        // Draw sun glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFA726).copy(alpha = 0.3f),
                    Color(0xFFFFA726).copy(alpha = 0f)
                ),
                center = Offset(centerX, centerY)
            ),
            radius = 60.dp.toPx() * scale,
            center = Offset(centerX, centerY)
        )

        // Draw sun
        drawCircle(
            color = Color(0xFFFFA726).copy(alpha = 0.8f),
            radius = 35.dp.toPx() * scale,
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
fun ThunderAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "thunder")
    val flashAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0f at 0
                0.6f at 100
                0f at 200
                0.8f at 300
                0f at 400
                0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "flash"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Cloud background
        CloudAnimation()

        // Lightning flash
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Flash overlay
            drawRect(
                color = Color.White.copy(alpha = flashAlpha * 0.3f)
            )

            // Lightning bolt
            if (flashAlpha > 0.3f) {
                val path = Path().apply {
                    val centerX = size.width * 0.6f
                    moveTo(centerX, 0f)
                    lineTo(centerX - 20.dp.toPx(), size.height * 0.3f)
                    lineTo(centerX + 10.dp.toPx(), size.height * 0.3f)
                    lineTo(centerX - 15.dp.toPx(), size.height * 0.7f)
                    lineTo(centerX + 5.dp.toPx(), size.height * 0.45f)
                    lineTo(centerX + 20.dp.toPx(), size.height * 0.45f)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color(0xFFFFEB3B).copy(alpha = flashAlpha)
                )
            }
        }
    }
}

// Data classes for animations
data class RainDrop(
    val x: Float,
    val speed: Float,
    val length: Float
)

data class SnowFlake(
    val x: Float,
    val speed: Float,
    val size: Float,
    val swing: Float
)

data class CloudData(
    val y: Float,
    val speed: Float,
    val size: Float
)