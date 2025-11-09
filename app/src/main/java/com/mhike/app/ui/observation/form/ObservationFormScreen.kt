package com.mhike.app.ui.observation.form

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.mhike.app.util.MediaStoreUtils
import kotlinx.datetime.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.time.ExperimentalTime

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalTime::class)
@Composable
fun ObservationFormScreen(
    hikeId: Long,
    obsId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: ObservationFormViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val isEdit = obsId != null
    var showValidationError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    
    val darkBg = Color(0xFF0A1929)
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var showDeletePhotoDialog by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

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
            vm.addPhoto(capturedImageUri.toString())
            capturedImageUri = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { vm.addPhoto(it.toString()) }
    }

    LaunchedEffect(obsId) {
        if (obsId != null) vm.loadForEdit(obsId)
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
        
        ObservationBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isEdit) "Edit Observation" else "Add Observation",
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
                            onClick = onCancel,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cancel",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = accentBlue.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(accentBlue.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = lightBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isEdit) "Update Your Observation" else "Record Your Observation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Document what you discovered",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                
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
                            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                        Icons.Default.Visibility,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "Observation Details",
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

                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Time of Observation *",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF0D1F2D).copy(alpha = 0.6f)
                                    ),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = SolidColor(accentBlue.copy(alpha = 0.3f))
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = lightBlue
                                            )
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = formatObservationTime(ui.observationTime),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = formatObservationDate(ui.observationTime),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.White.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit time",
                                            tint = lightBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.RemoveRedEye,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "What did you observe? *",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }

                                DarkObservationTextField(
                                    value = ui.text,
                                    onValueChange = {
                                        vm.ui.value = ui.copy(text = it)
                                        if (showValidationError && it.isNotBlank()) {
                                            showValidationError = false
                                        }
                                    },
                                    placeholder = "e.g., Spotted a rare bird, Found unique rock formation...",
                                    minLines = 3,
                                    maxLines = 6,
                                    isError = showValidationError && ui.text.isBlank(),
                                    errorMessage = if (showValidationError && ui.text.isBlank()) "Observation is required" else null
                                )
                            }

                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Comment,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Additional Comments",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }

                                DarkObservationTextField(
                                    value = ui.comment,
                                    onValueChange = { vm.ui.value = ui.copy(comment = it) },
                                    placeholder = "Add any additional notes or context...",
                                    minLines = 3,
                                    maxLines = 6
                                )
                            }
                        }
                    }
                }

                
                DarkPhotosSection(
                    photoUris = ui.photoUris,
                    onAddClick = { showPhotoDialog = true },
                    onPhotoClick = { selectedPhotoUri = it },
                    onPhotoDelete = {
                        photoToDelete = it
                        showDeletePhotoDialog = true
                    }
                )

                
                if (ui.text.isNotEmpty() || ui.comment.isNotEmpty() || ui.photoUris.isNotEmpty()) {
                    DarkStatsCard(
                        observationLength = ui.text.length,
                        commentLength = ui.comment.length,
                        photoCount = ui.photoUris.size
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(lightBlue.copy(alpha = 0.4f))
                        )
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (ui.text.isBlank()) {
                                showValidationError = true
                            } else {
                                vm.save(hikeId, obsId, onSaved)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp
                        )
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isEdit) "Update" else "Save",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    
    if (showTimePicker) {
        DarkTimePickerDialog(
            initialTime = ui.observationTime,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                vm.updateTime(newTime)
                showTimePicker = false
            }
        )
    }

    if (showPhotoDialog) {
        DarkPhotoPickerDialog(
            onDismiss = { showPhotoDialog = false },
            onCamera = {
                showPhotoDialog = false
                if (cameraPermission.status.isGranted) {
                    capturedImageUri = MediaStoreUtils.createImageUri(context, "mhike_observation")
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

    if (selectedPhotoUri != null) {
        DarkPhotoViewDialog(
            photoUri = selectedPhotoUri!!,
            onDismiss = { selectedPhotoUri = null },
            onDelete = {
                photoToDelete = selectedPhotoUri
                selectedPhotoUri = null
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
                photoToDelete?.let { vm.removePhoto(it) }
                showDeletePhotoDialog = false
                photoToDelete = null
            }
        )
    }
}

@Composable
private fun DarkObservationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color.White.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = if (maxLines > 1) ImeAction.Default else ImeAction.Next
            ),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                focusedContainerColor = darkOverlay.copy(alpha = 0.5f),
                unfocusedContainerColor = darkOverlay.copy(alpha = 0.3f),
                focusedBorderColor = Color(0xFF29B6F6),
                unfocusedBorderColor = Color(0xFF29B6F6).copy(alpha = 0.3f),
                cursorColor = Color(0xFF29B6F6),
                errorBorderColor = Color(0xFFEF5350),
                errorContainerColor = darkOverlay.copy(alpha = 0.5f)
            )
        )

        if (isError && errorMessage != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFE57373)
                )
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE57373)
                )
            }
        }
    }
}

@Composable
private fun DarkPhotosSection(
    photoUris: List<String>,
    onAddClick: () -> Unit,
    onPhotoClick: (String) -> Unit,
    onPhotoDelete: (String) -> Unit
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
                                Icons.Default.PhotoLibrary,
                                null,
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
                        if (photoUris.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = accentBlue.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = photoUris.size.toString(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = lightBlue
                                )
                            }
                        }
                    }

                    if (photoUris.isNotEmpty()) {
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

                if (photoUris.isEmpty()) {
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
                        items(photoUris) { photoUri ->
                            DarkObservationPhotoThumbnail(
                                photoUri = photoUri,
                                onClick = { onPhotoClick(photoUri) },
                                onDelete = { onPhotoDelete(photoUri) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkObservationPhotoThumbnail(
    photoUri: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
                model = photoUri.toUri(),
                contentDescription = "Observation photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onDelete,
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
private fun DarkStatsCard(
    observationLength: Int,
    commentLength: Int,
    photoCount: Int
) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = darkSurface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (observationLength > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$observationLength",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentBlue
                    )
                    Text(
                        text = "observation chars",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            if (commentLength > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$commentLength",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26A69A)
                    )
                    Text(
                        text = "comment chars",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            if (photoCount > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$photoCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7E57C2)
                    )
                    Text(
                        text = "photos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ObservationBackgroundStars() {
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
                color = Color.White.copy(alpha = (0.3f + twinkle * 0.4f).toFloat()),
                radius = (baseSize * (0.8f + twinkle * 0.4f)).toFloat(),
                center = position
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DarkTimePickerDialog(
    initialTime: Instant,
    onDismiss: () -> Unit,
    onConfirm: (Instant) -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour,
        initialMinute = initialTime.toLocalDateTime(TimeZone.currentSystemDefault()).minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = darkSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = lightBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Set Observation Time",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF0D1F2D),
                        selectorColor = accentBlue,
                        timeSelectorSelectedContainerColor = accentBlue,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContainerColor = Color(0xFF0D1F2D),
                        timeSelectorUnselectedContentColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val currentDateTime = initialTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    val newDateTime = LocalDateTime(
                        year = currentDateTime.year,
                        month = currentDateTime.month.number,
                        day = currentDateTime.day,
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        second = 0,
                        nanosecond = 0
                    )
                    val newInstant = newDateTime.toInstant(TimeZone.currentSystemDefault())
                    onConfirm(newInstant)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
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
private fun DarkPhotoPickerDialog(
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
                "Choose how you want to add a photo",
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
                        brush = SolidColor(lightBlue.copy(alpha = 0.4f))
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
private fun DarkPhotoViewDialog(
    photoUri: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val darkSurface = Color(0xFF1A2F42)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                        model = photoUri.toUri(),
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
                            brush = SolidColor(Color(0xFFEF5350).copy(alpha = 0.4f))
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
                "Are you sure you want to remove this photo?",
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

@OptIn(ExperimentalTime::class)
@SuppressLint("DefaultLocale")
fun formatObservationTime(instant: Instant): String {
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = dateTime.hour
    val minute = dateTime.minute
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}

@OptIn(ExperimentalTime::class)
fun formatObservationDate(instant: Instant): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val date = Date(instant.toEpochMilliseconds())
    return sdf.format(date)
}