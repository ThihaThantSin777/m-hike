package com.mhike.app.ui.observation.form

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
import kotlin.math.cos
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

    
    val deepNavy = Color(0xFF0A1628)
    val richBlue = Color(0xFF1A2942)
    val accentCyan = Color(0xFF00D9FF)
    val accentTeal = Color(0xFF00FFD1)

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
                        deepNavy,
                        richBlue,
                        Color(0xFF243B5E)
                    )
                )
            )
    ) {
        
        EnhancedFormBackground()

        Scaffold(
            topBar = {
                ModernFormTopBar(
                    title = if (isEdit) "Edit Observation" else "New Observation",
                    onBack = onCancel
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                
                TimeSelectionCard(
                    time = ui.observationTime,
                    onClick = { showTimePicker = true }
                )

                
                MainObservationCard(
                    text = ui.text,
                    onTextChange = {
                        vm.ui.value = ui.copy(text = it)
                        if (showValidationError && it.isNotBlank()) {
                            showValidationError = false
                        }
                    },
                    comment = ui.comment,
                    onCommentChange = { vm.ui.value = ui.copy(comment = it) },
                    showValidationError = showValidationError
                )

                
                ModernPhotosCard(
                    photoUris = ui.photoUris,
                    onAddClick = { showPhotoDialog = true },
                    onPhotoClick = { selectedPhotoUri = it },
                    onPhotoDelete = {
                        photoToDelete = it
                        showDeletePhotoDialog = true
                    }
                )

                
                if (ui.text.isNotEmpty() || ui.comment.isNotEmpty() || ui.photoUris.isNotEmpty()) {
                    CompactStatsCard(
                        observationLength = ui.text.length,
                        commentLength = ui.comment.length,
                        photoCount = ui.photoUris.size
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                
                ActionButtonsRow(
                    isEdit = isEdit,
                    onCancel = onCancel,
                    onSave = {
                        if (ui.text.isBlank()) {
                            showValidationError = true
                        } else {
                            vm.save(hikeId, obsId, onSaved)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    
    if (showTimePicker) {
        ModernTimePickerDialog(
            initialTime = ui.observationTime,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                vm.updateTime(newTime)
                showTimePicker = false
            }
        )
    }

    if (showPhotoDialog) {
        ModernPhotoDialog(
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
        ModernPhotoViewDialog(
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
        ModernDeletePhotoDialog(
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

/* ============================================================================
   TOP BAR
   ============================================================================ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernFormTopBar(
    title: String,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF00FFD1)
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

/* ============================================================================
   FORM CARDS
   ============================================================================ */

@OptIn(ExperimentalTime::class)
@Composable
private fun TimeSelectionCard(
    time: Instant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
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
                color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Observation Time",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF00FFD1).copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatObservationTime(time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    text = formatObservationDate(time),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit time",
                tint = Color(0xFF00D9FF),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun MainObservationCard(
    text: String,
    onTextChange: (String) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    showValidationError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF00D9FF),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "Your Observation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "What did you observe? *",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF00FFD1),
                    fontWeight = FontWeight.Medium
                )

                ModernTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = "Describe what you saw, heard, or discovered...",
                    minLines = 4,
                    maxLines = 8,
                    isError = showValidationError && text.isBlank(),
                    errorMessage = if (showValidationError && text.isBlank())
                        "Please describe your observation" else null
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
                                Color(0xFF00D9FF).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = Color(0xFF00FFD1),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Additional Notes (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF00FFD1),
                        fontWeight = FontWeight.Medium
                    )
                }

                ModernTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    placeholder = "Add context, feelings, or extra details...",
                    minLines = 3,
                    maxLines = 5
                )
            }
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                focusedContainerColor = Color(0xFF0D1B2E).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFF0D1B2E).copy(alpha = 0.3f),
                focusedBorderColor = Color(0xFF00D9FF),
                unfocusedBorderColor = Color(0xFF00D9FF).copy(alpha = 0.3f),
                cursorColor = Color(0xFF00D9FF),
                errorBorderColor = Color(0xFFEF5350),
                errorContainerColor = Color(0xFF0D1B2E).copy(alpha = 0.5f)
            )
        )

        if (isError && errorMessage != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFEF5350)
                )
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF5350),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ModernPhotosCard(
    photoUris: List<String>,
    onAddClick: () -> Unit,
    onPhotoClick: (String) -> Unit,
    onPhotoDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00FFD1).copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color(0xFF00FFD1),
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
                            text = "${photoUris.size} ${if (photoUris.size == 1) "photo" else "photos"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF00FFD1).copy(alpha = 0.8f)
                        )
                    }
                }

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00FFD1).copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add photo",
                        tint = Color(0xFF00FFD1),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (photoUris.isEmpty()) {
                EmptyPhotosState(onAddClick = onAddClick)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(photoUris) { photoUri ->
                        ModernPhotoThumbnail(
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

@Composable
private fun EmptyPhotosState(onAddClick: () -> Unit) {
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
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(30.dp)
                        .background(
                            Color(0xFF00FFD1).copy(alpha = 0.3f),
                            CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF00FFD1).copy(alpha = 0.7f)
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
                modifier = Modifier.height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(22.dp)
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
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
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
    photoUri: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
                model = photoUri.toUri(),
                contentDescription = "Observation photo",
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
                onClick = onDelete,
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
                            contentDescription = "Delete",
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
private fun CompactStatsCard(
    observationLength: Int,
    commentLength: Int,
    photoCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (observationLength > 0) {
                StatItem(
                    value = "$observationLength",
                    label = "chars",
                    color = Color(0xFF00D9FF)
                )
            }
            if (commentLength > 0) {
                StatItem(
                    value = "$commentLength",
                    label = "notes",
                    color = Color(0xFF00FFD1)
                )
            }
            if (photoCount > 0) {
                StatItem(
                    value = "$photoCount",
                    label = "photos",
                    color = Color(0xFF7E57C2)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ActionButtonsRow(
    isEdit: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    Color(0xFF00D9FF).copy(alpha = 0.4f)
                )
            )
        ) {
            Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cancel", fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp
            )
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF0A1628)
                    )
                    Text(
                        if (isEdit) "Update" else "Save",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A1628)
                    )
                }
            }
        }
    }
}

/* ============================================================================
   DIALOGS
   ============================================================================ */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun ModernTimePickerDialog(
    initialTime: Instant,
    onDismiss: () -> Unit,
    onConfirm: (Instant) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour,
        initialMinute = initialTime.toLocalDateTime(TimeZone.currentSystemDefault()).minute,
        is24Hour = false
    )

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
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Set Observation Time",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 22.sp
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
                        selectorColor = Color(0xFF00D9FF),
                        timeSelectorSelectedContainerColor = Color(0xFF00D9FF),
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
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D9FF)
                ),
                shape = RoundedCornerShape(24.dp)
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
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        }
    )
}

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
                color = Color(0xFF00FFD1).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF00FFD1),
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
                                contentDescription = null,
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
private fun ModernPhotoViewDialog(
    photoUri: String,
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
                        model = photoUri.toUri(),
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
private fun ModernDeletePhotoDialog(
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
                        contentDescription = null,
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
                "Are you sure you want to remove this photo?",
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
   FORMATTERS
   ============================================================================ */

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

/* ============================================================================
   BACKGROUND ANIMATIONS
   ============================================================================ */

@Composable
private fun EnhancedFormBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        FormFloatingParticles()
        FormGradientOrbs()
    }
}

@Composable
private fun FormFloatingParticles() {
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
private fun FormGradientOrbs() {
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