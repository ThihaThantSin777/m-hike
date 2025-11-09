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

    
    val darkBg = Color(0xFF0A1929)
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

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
                        darkBg,
                        Color(0xFF1A2F42),
                        Color(0xFF2A4A5E)
                    )
                )
            )
    ) {
        
        DetailBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Hike Details",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                letterSpacing = 0.5.sp,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF4FC3F7),
                                                Color(0xFF29B6F6),
                                                Color(0xFF03A9F4)
                                            )
                                        )
                                    )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = lightBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
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
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .blur(20.dp)
                                        .clip(CircleShape)
                                        .background(accentBlue.copy(alpha = 0.3f))
                                )
                                CircularProgressIndicator(
                                    color = lightBlue,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Text(
                                text = "Loading hike details...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
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
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .blur(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF5350).copy(alpha = 0.3f))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF5350).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color(0xFFE57373)
                                    )
                                }
                            }
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
                                fontWeight = FontWeight.Light
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentBlue
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Go Back", fontWeight = FontWeight.Bold)
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
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = accentBlue.copy(alpha = 0.15f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalArrangement = Arrangement.spacedBy(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .blur(15.dp)
                                            .clip(CircleShape)
                                            .background(accentBlue.copy(alpha = 0.4f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(accentBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Terrain,
                                            contentDescription = null,
                                            modifier = Modifier.size(34.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = hike.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
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
                                            tint = Color(0xFFFFB74D)
                                        )
                                        Text(
                                            text = hike.location,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        
                        AnimatedWeatherCard(
                            weatherState = weatherState,
                            onRefresh = { weatherVm.refresh() }
                        )

                        
                        DarkPhotosCard(
                            photos = photos,
                            onAddClick = { showPhotoDialog = true },
                            onPhotoClick = { selectedPhoto = it },
                            onPhotoLongClick = {
                                photoToDelete = it
                                showDeletePhotoDialog = true
                            }
                        )

                        
                        DarkKeyInfoCard(hike = hike)

                        
                        if ((hike.description?.isNotBlank() == true) ||
                            (hike.terrain?.isNotBlank() == true) ||
                            (hike.expectedWeather?.isNotBlank() == true)
                        ) {
                            DarkAdditionalDetailsCard(hike = hike)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    
    if (showPhotoDialog) {
        DarkPhotoDialog(
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
        DarkFullPhotoDialog(
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
        DarkDeletePhotoDialog(
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

@Composable
private fun DarkPhotosCard(
    photos: List<Media>,
    onAddClick: () -> Unit,
    onPhotoClick: (Media) -> Unit,
    onPhotoLongClick: (Media) -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = darkSurface)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7).copy(alpha = 0.8f),
                                Color(0xFF29B6F6).copy(alpha = 0.8f),
                                Color(0xFF03A9F4).copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(accentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = lightBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Photos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = accentBlue.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = photos.size.toString(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = lightBlue
                            )
                        }
                    }

                    if (photos.isNotEmpty()) {
                        IconButton(
                            onClick = onAddClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add photo",
                                tint = lightBlue
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    lightBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                if (photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .blur(20.dp)
                                        .clip(CircleShape)
                                        .background(accentBlue.copy(alpha = 0.3f))
                                )
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = lightBlue.copy(alpha = 0.7f)
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentBlue
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add Photos", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(photos) { photo ->
                            DarkPhotoThumbnail(
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
}

@Composable
private fun DarkPhotoThumbnail(
    photo: Media,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1F2D))
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
                    color = Color(0xFFEF5350).copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete",
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
private fun DarkKeyInfoCard(hike: com.mhike.app.domain.model.Hike) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = darkSurface)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7).copy(alpha = 0.8f),
                                Color(0xFF29B6F6).copy(alpha = 0.8f),
                                Color(0xFF03A9F4).copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            null,
                            tint = lightBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Key Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    lightBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                hike.date?.let { date ->
                    DarkDetailItem(
                        icon = Icons.Default.DateRange,
                        label = "Date",
                        value = date.toString(),
                        iconTint = lightBlue
                    )
                }

                DarkDetailItem(
                    icon = Icons.Default.Straighten,
                    label = "Distance",
                    value = "${hike.lengthKm} km",
                    iconTint = accentBlue
                )

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (chipBg, chipFg) = when (hike.difficulty.lowercase()) {
                        "easy" -> Color(0xFF66BB6A).copy(alpha = 0.3f) to Color(0xFF81C784)
                        "moderate" -> Color(0xFFFFB74D).copy(alpha = 0.3f) to Color(0xFFFFD54F)
                        "hard" -> Color(0xFFEF5350).copy(alpha = 0.3f) to Color(0xFFE57373)
                        else -> Color.White.copy(alpha = 0.1f) to Color.White
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(chipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = chipFg
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Difficulty Level",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = chipBg
                        ) {
                            Text(
                                text = hike.difficulty,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = chipFg,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val parkingBg = if (hike.parking)
                        Color(0xFF66BB6A).copy(alpha = 0.3f)
                    else
                        Color(0xFFEF5350).copy(alpha = 0.3f)
                    val parkingFg = if (hike.parking)
                        Color(0xFF81C784)
                    else
                        Color(0xFFE57373)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(parkingBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hike.parking) Icons.Default.LocalParking else Icons.Default.RemoveCircle,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = parkingFg
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Parking Available",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (hike.parking) "Yes" else "No",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = parkingFg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkAdditionalDetailsCard(hike: com.mhike.app.domain.model.Hike) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = darkSurface)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7).copy(alpha = 0.8f),
                                Color(0xFF29B6F6).copy(alpha = 0.8f),
                                Color(0xFF03A9F4).copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Description,
                            null,
                            tint = lightBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Additional Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    lightBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (hike.description?.isNotBlank() == true) {
                        DarkDetailTextBlock(
                            icon = Icons.AutoMirrored.Filled.Notes,
                            label = "Description",
                            value = hike.description,
                            iconTint = lightBlue
                        )
                    }
                    if (hike.terrain?.isNotBlank() == true) {
                        DarkDetailTextBlock(
                            icon = Icons.Default.Landscape,
                            label = "Terrain Type",
                            value = hike.terrain,
                            iconTint = accentBlue
                        )
                    }
                    if (hike.expectedWeather?.isNotBlank() == true) {
                        DarkDetailTextBlock(
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
}

@Composable
private fun DarkDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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

@Composable
private fun DarkDetailTextBlock(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
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
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0D1F2D).copy(alpha = 0.5f)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(14.dp),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun DetailBackgroundStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "shimmer"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val stars = listOf(
            Offset(width * 0.15f, height * 0.2f) to 3f,
            Offset(width * 0.85f, height * 0.15f) to 2.5f,
            Offset(width * 0.25f, height * 0.35f) to 2f,
            Offset(width * 0.75f, height * 0.4f) to 3.5f,
            Offset(width * 0.9f, height * 0.6f) to 2f,
            Offset(width * 0.1f, height * 0.7f) to 2.5f
        )

        stars.forEach { (position, baseSize) ->
            val twinkle = sin(shimmer / 100f + position.x + position.y) * 0.5f + 0.5f
            drawCircle(
                color = Color.White.copy(alpha = 0.3f + twinkle * 0.4f),
                radius = baseSize * (0.8f + twinkle * 0.4f),
                center = position
            )
        }
    }
}



@Composable
private fun DarkPhotoDialog(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = darkSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = lightBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Add Photo",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                "Choose how you want to add a photo to this hike",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onCamera,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onGallery,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = lightBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(lightBlue.copy(alpha = 0.4f))
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Choose from Gallery", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Cancel")
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DarkFullPhotoDialog(
    photo: Media,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = darkSurface)
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
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
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
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE57373)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF5350).copy(alpha = 0.4f))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkDeletePhotoDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = darkSurface,
        icon = {
            Icon(
                Icons.Default.Delete,
                null,
                tint = Color(0xFFE57373),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Delete Photo?",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                "Are you sure you want to delete this photo? This action cannot be undone.",
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350)
                ),
                shape = RoundedCornerShape(12.dp)
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
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun AnimatedWeatherCard(
    weatherState: WeatherUiState,
    onRefresh: () -> Unit = {}
) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = accentBlue.copy(alpha = 0.12f))
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentBlue.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Cloud,
                            null,
                            tint = lightBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        "Current Weather",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
                IconButton(
                    onClick = onRefresh,
                    enabled = weatherState !is WeatherUiState.Loading,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (weatherState !is WeatherUiState.Loading) accentBlue.copy(alpha = 0.2f) else Color.Transparent)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        "Refresh weather",
                        tint = if (weatherState is WeatherUiState.Loading)
                            Color.White.copy(alpha = 0.4f)
                        else
                            lightBlue
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                lightBlue.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            when (weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = lightBlue,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Loading weather...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                is WeatherUiState.Success -> DarkWeatherContent(weather = weatherState.data)

                is WeatherUiState.Error -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                null,
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFFE57373)
                            )
                            Text(
                                "Unable to load weather",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE57373)
                            )
                            Text(
                                weatherState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                is WeatherUiState.Idle -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Weather information will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DarkWeatherContent(weather: WeatherInfo) {
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)
    val description = (weather.description ?: weather.summary ?: "").lowercase()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", weather.tempC),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            getWeatherIcon(description),
                            null,
                            modifier = Modifier.size(24.dp),
                            tint = lightBlue
                        )
                        Text(
                            text = (weather.description ?: weather.summary ?: "N/A").replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = accentBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AnimatedWeatherIcon(description = description)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            weather.feelsLikeC?.let { feelsLike ->
                DarkWeatherDetailCard(
                    icon = Icons.Default.Thermostat,
                    label = "Feels Like",
                    value = String.format("%.1f°C", feelsLike),
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFFFFB74D)
                )
            }
            weather.humidityPercent?.let { humidity ->
                DarkWeatherDetailCard(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "$humidity%",
                    modifier = Modifier.weight(1f),
                    iconTint = lightBlue
                )
            }
        }

        if (weather.windSpeedMs != null || (weather.tempMinC != null && weather.tempMaxC != null)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                weather.windSpeedMs?.let { windSpeed ->
                    DarkWeatherDetailCard(
                        icon = Icons.Default.Air,
                        label = "Wind Speed",
                        value = String.format("%.1f m/s", windSpeed),
                        modifier = Modifier.weight(1f),
                        iconTint = accentBlue
                    )
                }
                if (weather.tempMinC != null && weather.tempMaxC != null) {
                    DarkWeatherDetailCard(
                        icon = Icons.Default.Thermostat,
                        label = "Min / Max",
                        value = String.format("%.0f° / %.0f°", weather.tempMinC, weather.tempMaxC),
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF81C784)
                    )
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
            Spacer(Modifier.width(4.dp))
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
fun DarkWeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color
) {
    val darkOverlay = Color(0xFF0D1F2D)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = darkOverlay.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

            Canvas(modifier = Modifier.size(60.dp)) {
                rotate(rotation) {
                    for (i in 0 until 8) {
                        val angle = (i * 45f) * (Math.PI / 180f).toFloat()
                        val startX = center.x + cos(angle) * 25.dp.toPx()
                        val startY = center.y + sin(angle) * 25.dp.toPx()
                        val endX = center.x + cos(angle) * 35.dp.toPx()
                        val endY = center.y + sin(angle) * 35.dp.toPx()

                        drawLine(
                            color = Color(0xFFFFF176),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 4.dp.toPx()
                        )
                    }
                }
                drawCircle(
                    color = Color(0xFFFFF176),
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
                tint = Color(0xFF81D4FA)
            )
        }
    }
}

@Composable
fun RainAnimation() {
    val lightBlue = Color(0xFF81D4FA)
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
                        lightBlue.copy(alpha = 0.6f),
                        lightBlue.copy(alpha = 0.2f)
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
            CloudData(y = 0.3f, speed = 0.3f, size = 80f),
            CloudData(y = 0.5f, speed = 0.4f, size = 100f),
            CloudData(y = 0.7f, speed = 0.25f, size = 70f)
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
        val centerY = size.height * 0.3f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF176).copy(alpha = 0.3f),
                    Color(0xFFFFF176).copy(alpha = 0f)
                ),
                center = Offset(centerX, centerY)
            ),
            radius = 60.dp.toPx() * scale,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color(0xFFFFF176).copy(alpha = 0.8f),
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
        CloudAnimation()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.White.copy(alpha = flashAlpha * 0.3f))
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
                drawPath(path = path, color = Color(0xFFFFF176).copy(alpha = flashAlpha))
            }
        }
    }
}

data class RainDrop(val x: Float, val speed: Float, val length: Float)
data class SnowFlake(val x: Float, val speed: Float, val size: Float, val swing: Float)
data class CloudData(val y: Float, val speed: Float, val size: Float)