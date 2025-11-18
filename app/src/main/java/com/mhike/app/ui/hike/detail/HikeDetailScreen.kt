package com.mhike.app.ui.hike.detail

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
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

    
    val deepNavy = Color(0xFF0A1628)
    val richBlue = Color(0xFF1A2942)
    val accentCyan = Color(0xFF00D9FF)
    val accentTeal = Color(0xFF00FFD1)

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
        uri?.let { vm.attachPhoto(it.toString(), context.contentResolver.getType(it)) }
    }

    LaunchedEffect(state) {
        if (state is HikeDetailUiState.Ready) {
            val hike = (state as HikeDetailUiState.Ready).hike
            weatherVm.loadByCity(hike.location)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        deepNavy,
                        richBlue,
                        Color(0xFF243B5E)
                    )
                )
            )
    ) {
        
        EnhancedDetailBackground()

        Scaffold(
            topBar = {
                ModernDetailTopBar(onBack = onBack)
            },
            containerColor = Color.Transparent
        ) { pv ->
            when (val s = state) {
                is HikeDetailUiState.Loading -> {
                    LoadingState(modifier = Modifier.padding(pv))
                }

                is HikeDetailUiState.NotFound -> {
                    NotFoundState(
                        onBack = onBack,
                        modifier = Modifier.padding(pv)
                    )
                }

                is HikeDetailUiState.Ready -> {
                    val hike = s.hike
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pv)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        
                        ModernHeroCard(hike = hike)

                        
                        ModernWeatherCard(
                            weatherState = weatherState,
                            onRefresh = { weatherVm.refresh() }
                        )

                        
                        StatsOverviewGrid(hike = hike)

                        
                        ModernPhotosSection(
                            photos = photos,
                            onAddClick = { showPhotoDialog = true },
                            onPhotoClick = { selectedPhoto = it },
                            onPhotoLongClick = {
                                photoToDelete = it
                                showDeletePhotoDialog = true
                            }
                        )

                        
                        if ((hike.description?.isNotBlank() == true) ||
                            (hike.terrain?.isNotBlank() == true) ||
                            (hike.expectedWeather?.isNotBlank() == true)
                        ) {
                            ModernDetailsCard(hike = hike)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    
    if (showPhotoDialog) {
        ModernPhotoDialog(
            onDismiss = { showPhotoDialog = false },
            onCamera = {
                showPhotoDialog = false
                if (cameraPermission.status.isGranted) {
                    capturedImageUri = MediaStoreUtils.createImageUri(context, "mhike_hike")
                    capturedImageUri?.let { cameraLauncher.launch(it) }
                } else cameraPermission.launchPermissionRequest()
            },
            onGallery = {
                showPhotoDialog = false
                if (photoPermission.status.isGranted) galleryLauncher.launch("image/*")
                else photoPermission.launchPermissionRequest()
            }
        )
    }

    if (selectedPhoto != null) {
        ModernFullPhotoDialog(
            photo = selectedPhoto!!,
            onDismiss = { selectedPhoto = null },
            onDelete = {
                photoToDelete = selectedPhoto
                selectedPhoto = null
                showDeletePhotoDialog = true
            }
        )
    }

    if (showDeletePhotoDialog && photoToDelete != null) {
        ModernDeleteDialog(
            onDismiss = {
                showDeletePhotoDialog = false
                photoToDelete = null
            },
            onConfirm = {
                photoToDelete?.let { vm.deletePhoto(it) }
                showDeletePhotoDialog = false
                photoToDelete = null
            }
        )
    }
}

/* ============================================================================
   COMPONENTS
   ============================================================================ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernDetailTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Hike Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00D9FF).copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF00D9FF),
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(30.dp)
                        .background(
                            Color(0xFF00D9FF).copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                CircularProgressIndicator(
                    color = Color(0xFF00D9FF),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(56.dp)
                )
            }
            Text(
                text = "Loading details...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NotFoundState(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .blur(40.dp)
                        .background(
                            Color(0xFFEF5350).copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF5350).copy(alpha = 0.2f),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color(0xFFE57373)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Hike Not Found",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "The hike you're looking for doesn't exist\nor has been deleted.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Button(
                onClick = onBack,
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF00FFD1)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF0A1628)
                        )
                        Text(
                            "Go Back",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A1628)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernHeroCard(hike: com.mhike.app.domain.model.Hike) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00D9FF).copy(alpha = 0.15f),
                                Color(0xFF00FFD1).copy(alpha = 0.10f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00D9FF).copy(alpha = 0.25f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Terrain,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF00D9FF)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = hike.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 24.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFFFF6B6B)
                            )
                            Text(
                                text = hike.location,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0D1B2E).copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF00FFD1)
                        )
                        Text(
                            text = hike.date.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsOverviewGrid(hike: com.mhike.app.domain.model.Hike) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Straighten,
                label = "Distance",
                value = "${hike.lengthKm} km",
                color = Color(0xFF00D9FF),
                modifier = Modifier.weight(1f)
            )

            val difficultyColor = when (hike.difficulty.lowercase()) {
                "easy" -> Color(0xFF4CAF50)
                "moderate" -> Color(0xFFFF9800)
                "hard" -> Color(0xFFEF5350)
                else -> Color(0xFF9E9E9E)
            }

            StatCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Difficulty",
                value = hike.difficulty,
                color = difficultyColor,
                modifier = Modifier.weight(1f)
            )
        }

        
        StatCard(
            icon = if (hike.parking) Icons.Default.LocalParking else Icons.Default.RemoveCircle,
            label = "Parking Available",
            value = if (hike.parking) "Yes" else "No",
            color = if (hike.parking) Color(0xFF00FFD1) else Color(0xFFEF5350),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = color
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun ModernPhotosSection(
    photos: List<Media>,
    onAddClick: () -> Unit,
    onPhotoClick: (Media) -> Unit,
    onPhotoLongClick: (Media) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color(0xFF00D9FF),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Photos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${photos.size} ${if (photos.size == 1) "photo" else "photos"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF00D9FF).copy(alpha = 0.8f)
                        )
                    }
                }

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D9FF).copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add photo",
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (photos.isEmpty()) {
                EmptyPhotosState(onAddClick = onAddClick)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(photos) { photo ->
                        ModernPhotoThumbnail(
                            photo = photo,
                            onClick = { onPhotoClick(photo) },
                            onLongClick = { onPhotoLongClick(photo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPhotosState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(30.dp)
                        .background(
                            Color(0xFF00D9FF).copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF00D9FF).copy(alpha = 0.7f)
                )
            }

            Text(
                text = "No photos yet",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onAddClick,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF00FFD1)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF0A1628)
                        )
                        Text(
                            "Add Photos",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A1628)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernPhotoThumbnail(
    photo: Media,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.uri.toUri(),
                contentDescription = "Hike photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            IconButton(
                onClick = onLongClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF5350).copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernDetailsCard(hike: com.mhike.app.domain.model.Hike) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00FFD1).copy(alpha = 0.2f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Description,
                            null,
                            tint = Color(0xFF00FFD1),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "Additional Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (hike.description?.isNotBlank() == true) {
                    DetailBlock(
                        icon = Icons.AutoMirrored.Filled.Notes,
                        label = "Description",
                        value = hike.description,
                        iconTint = Color(0xFF00D9FF)
                    )
                }
                if (hike.terrain?.isNotBlank() == true) {
                    DetailBlock(
                        icon = Icons.Default.Landscape,
                        label = "Terrain Type",
                        value = hike.terrain,
                        iconTint = Color(0xFF00FFD1)
                    )
                }
                if (hike.expectedWeather?.isNotBlank() == true) {
                    DetailBlock(
                        icon = Icons.Default.WbSunny,
                        label = "Expected Weather",
                        value = hike.expectedWeather,
                        iconTint = Color(0xFFFFB74D)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailBlock(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0D1B2E).copy(alpha = 0.5f)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(16.dp),
                lineHeight = 22.sp
            )
        }
    }
}

/* ============================================================================
   WEATHER CARD
   ============================================================================ */

@Composable
fun ModernWeatherCard(
    weatherState: WeatherUiState,
    onRefresh: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00D9FF).copy(alpha = 0.12f)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00D9FF).copy(alpha = 0.25f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Cloud,
                                null,
                                tint = Color(0xFF00D9FF),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        "Current Weather",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onRefresh,
                    enabled = weatherState !is WeatherUiState.Loading,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (weatherState !is WeatherUiState.Loading)
                                Color(0xFF00D9FF).copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        "Refresh weather",
                        tint = if (weatherState is WeatherUiState.Loading)
                            Color.White.copy(alpha = 0.4f)
                        else
                            Color(0xFF00D9FF)
                    )
                }
            }

            when (weatherState) {
                is WeatherUiState.Loading -> WeatherLoadingState()
                is WeatherUiState.Success -> ModernWeatherContent(weather = weatherState.data)
                is WeatherUiState.Error -> WeatherErrorState(message = weatherState.message)
                is WeatherUiState.Idle -> WeatherIdleState()
            }
        }
    }
}

@Composable
private fun WeatherLoadingState() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00D9FF),
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Text(
                "Loading weather...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun WeatherErrorState(message: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                null,
                modifier = Modifier.size(44.dp),
                tint = Color(0xFFE57373)
            )
            Text(
                "Unable to load weather",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE57373)
            )
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeatherIdleState() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Weather information will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ModernWeatherContent(weather: WeatherInfo) {
    val description = (weather.description ?: weather.summary ?: "").lowercase()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            when {
                description.contains("rain") -> RainAnimation()
                description.contains("snow") -> SnowAnimation()
                description.contains("cloud") -> CloudAnimation()
                description.contains("clear") || description.contains("sun") -> SunAnimation()
                description.contains("thunder") || description.contains("storm") -> ThunderAnimation()
                else -> CloudAnimation()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", weather.tempC),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 56.sp
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            getWeatherIcon(description),
                            null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF00FFD1)
                        )
                        Text(
                            text = (weather.description ?: weather.summary ?: "N/A")
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AnimatedWeatherIcon(description = description)
                    }
                }
            }
        }

        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                weather.feelsLikeC?.let { feelsLike ->
                    WeatherDetailCard(
                        icon = Icons.Default.Thermostat,
                        label = "Feels Like",
                        value = String.format("%.1f°C", feelsLike),
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFFFFB74D)
                    )
                }
                weather.humidityPercent?.let { humidity ->
                    WeatherDetailCard(
                        icon = Icons.Default.WaterDrop,
                        label = "Humidity",
                        value = "$humidity%",
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF00FFD1)
                    )
                }
            }

            if (weather.windSpeedMs != null || (weather.tempMinC != null && weather.tempMaxC != null)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    weather.windSpeedMs?.let { windSpeed ->
                        WeatherDetailCard(
                            icon = Icons.Default.Air,
                            label = "Wind Speed",
                            value = String.format("%.1f m/s", windSpeed),
                            modifier = Modifier.weight(1f),
                            iconTint = Color(0xFF00D9FF)
                        )
                    }
                    if (weather.tempMinC != null && weather.tempMaxC != null) {
                        WeatherDetailCard(
                            icon = Icons.Default.Thermostat,
                            label = "Min / Max",
                            value = String.format("%.0f° / %.0f°", weather.tempMinC, weather.tempMaxC),
                            modifier = Modifier.weight(1f),
                            iconTint = Color(0xFF81C784)
                        )
                    }
                }
            }
        }

        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                null,
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = buildString {
                    append(weather.placeName)
                    weather.countryCode?.let { append(", $it") }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun WeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B2E).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(28.dp),
                tint = iconTint
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/* ============================================================================
   DIALOGS
   ============================================================================ */

@Composable
private fun ModernPhotoDialog(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2942),
        shape = RoundedCornerShape(24.dp),
        icon = {
            Surface(
                shape = CircleShape,
                color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Add Photo",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 22.sp
            )
        },
        text = {
            Text(
                "Choose how you want to add a photo",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onCamera,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00D9FF),
                                        Color(0xFF00FFD1)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                null,
                                tint = Color(0xFF0A1628)
                            )
                            Text(
                                "Take Photo",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0A1628)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onGallery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF00FFD1)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            Color(0xFF00FFD1).copy(alpha = 0.5f)
                        )
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Choose from Gallery", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }
        }
    )
}

@Composable
private fun ModernFullPhotoDialog(
    photo: Media,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A2942)
            )
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                ) {
                    AsyncImage(
                        model = photo.uri.toUri(),
                        contentDescription = "Full photo",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(10.dp))
                        Text("Delete Photo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2942),
        shape = RoundedCornerShape(24.dp),
        icon = {
            Surface(
                shape = CircleShape,
                color = Color(0xFFEF5350).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Delete Photo?",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 22.sp
            )
        },
        text = {
            Text(
                "Are you sure you want to delete this photo? This action cannot be undone.",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Delete", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        }
    )
}

/* ============================================================================
   BACKGROUND & ANIMATIONS - Keep from original
   ============================================================================ */

@Composable
private fun EnhancedDetailBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingParticles()
        AnimatedGradientOrbs()
    }
}

@Composable
private fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle1"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        for (i in 0..10) {
            val baseX = width * (i / 10f)
            val baseY = (offset1 + i * 100) % height
            val sineOffset = sin((offset1 + i * 60) / 100f) * 25f

            drawCircle(
                color = Color(0xFF00D9FF).copy(alpha = 0.1f),
                radius = 2f + (i % 3),
                center = Offset(baseX + sineOffset, baseY)
            )
        }
    }
}

@Composable
private fun AnimatedGradientOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing)
        ),
        label = "orb_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00D9FF).copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(width * 0.85f, height * 0.2f)
            ),
            radius = 180f,
            center = Offset(
                width * 0.85f + cos(Math.toRadians(rotation.toDouble())).toFloat() * 25f,
                height * 0.2f + sin(Math.toRadians(rotation.toDouble())).toFloat() * 25f
            )
        )
    }
}


fun getWeatherIcon(description: String): ImageVector = when {
    description.contains("clear", true) -> Icons.Default.WbSunny
    description.contains("cloud", true) -> Icons.Default.Cloud
    description.contains("rain", true) -> Icons.Default.Umbrella
    description.contains("snow", true) -> Icons.Default.AcUnit
    description.contains("thunder", true) -> Icons.Default.Bolt
    description.contains("mist", true) || description.contains("fog", true) -> Icons.Default.Cloud
    else -> Icons.Default.WbCloudy
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

            Canvas(modifier = Modifier.size(50.dp)) {
                rotate(rotation) {
                    for (i in 0 until 8) {
                        val angle = (i * 45f) * (Math.PI / 180f).toFloat()
                        val startX = center.x + cos(angle) * 20.dp.toPx()
                        val startY = center.y + sin(angle) * 20.dp.toPx()
                        val endX = center.x + cos(angle) * 28.dp.toPx()
                        val endY = center.y + sin(angle) * 28.dp.toPx()

                        drawLine(
                            color = Color(0xFFFFF176),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }
                drawCircle(
                    color = Color(0xFFFFF176),
                    radius = 16.dp.toPx(),
                    center = center
                )
            }
        }

        else -> {
            Icon(
                imageVector = getWeatherIcon(description),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color(0xFF00FFD1)
            )
        }
    }
}

@Composable
fun RainAnimation() {
    val rainDrops = remember {
        List(25) {
            RainDrop(
                x = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.5f,
                length = Random.nextFloat() * 20f + 15f
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val progress by infiniteTransition.animateFloat(
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
            val startY = -50f + (size.height + 50f) * ((progress * drop.speed) % 1f)
            val x = drop.x * size.width
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00D9FF).copy(alpha = 0.6f),
                        Color(0xFF00D9FF).copy(alpha = 0.2f)
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
        List(20) {
            SnowFlake(
                x = Random.nextFloat(),
                speed = Random.nextFloat() * 0.3f + 0.2f,
                size = Random.nextFloat() * 6f + 3f,
                swing = Random.nextFloat() * 0.02f + 0.01f
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val progress by infiniteTransition.animateFloat(
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
            val p = (progress * flake.speed) % 1f
            val y = -20f + (size.height + 20f) * p
            val x = (flake.x + sin(p * 10f) * flake.swing) * size.width
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
            CloudData(y = 0.35f, speed = 0.3f, size = 70f),
            CloudData(y = 0.55f, speed = 0.4f, size = 85f),
            CloudData(y = 0.7f, speed = 0.25f, size = 60f)
        )
    }
    val infiniteTransition = rememberInfiniteTransition(label = "clouds")
    val progress by infiniteTransition.animateFloat(
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
            val p = (progress * cloud.speed) % 1f
            val x = -cloud.size - 50f + (size.width + cloud.size + 100f) * p
            val y = size.height * cloud.y
            val c = Color.White.copy(alpha = 0.4f)
            drawCircle(color = c, radius = cloud.size.dp.toPx() * 0.5f, center = Offset(x, y))
            drawCircle(
                color = c,
                radius = cloud.size.dp.toPx() * 0.6f,
                center = Offset(x + cloud.size.dp.toPx() * 0.4f, y - cloud.size.dp.toPx() * 0.2f)
            )
            drawCircle(
                color = c,
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
        val centerY = size.height * 0.35f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF176).copy(alpha = 0.3f),
                    Color(0xFFFFF176).copy(alpha = 0f)
                ),
                center = Offset(centerX, centerY)
            ),
            radius = 55.dp.toPx() * scale,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color(0xFFFFF176).copy(alpha = 0.8f),
            radius = 32.dp.toPx() * scale,
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
        CloudAnimation()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.White.copy(alpha = flashAlpha * 0.3f))
            if (flashAlpha > 0.3f) {
                val path = Path().apply {
                    val centerX = size.width * 0.6f
                    moveTo(centerX, 0f)
                    lineTo(centerX - 18.dp.toPx(), size.height * 0.3f)
                    lineTo(centerX + 8.dp.toPx(), size.height * 0.3f)
                    lineTo(centerX - 12.dp.toPx(), size.height * 0.7f)
                    lineTo(centerX + 5.dp.toPx(), size.height * 0.45f)
                    lineTo(centerX + 18.dp.toPx(), size.height * 0.45f)
                    close()
                }
                drawPath(path = path, color = Color(0xFFFFF176).copy(alpha = flashAlpha))
            }
        }
    }
}

data class RainDrop(val x: Float, val speed: Float, val length: Float)
data class SnowFlake(val x: Float, val speed: Float, val size: Float, val swing: Float)
data class CloudData(val y: Float, val speed: Float, val size: Float)