package com.mhike.app.ui.hike.form

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDate
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeFormScreen(
    hikeId: Long? = null,
    onHikeSaved: () -> Unit,
    onBack: () -> Unit,
    vm: HikeFormViewModel = hiltViewModel()
) {
    val isEditMode = hikeId != null
    var formError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    
    val darkBg = Color(0xFF0A1929)
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)
    val cardBg = Color(0xFF2A4A5E)

    LaunchedEffect(hikeId) {
        if (hikeId != null) vm.loadForEdit(hikeId)
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
        
        FormBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isEditMode) "Edit Hike" else "New Hike",
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
                                imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.Info,
                                contentDescription = null,
                                tint = lightBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isEditMode) "Update Hike Details" else "Create New Hike",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Fields marked with * are required",
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
                                        Icons.Default.Terrain,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "Basic Information",
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

                            DarkFormTextField(
                                value = vm.draft.name,
                                onValueChange = { value -> vm.update { it.copy(name = value) } },
                                label = "Hike Name *",
                                placeholder = "e.g., Mount Everest Summit",
                                leadingIcon = Icons.Default.Hiking,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            DarkFormTextField(
                                value = vm.draft.location,
                                onValueChange = { value -> vm.update { it.copy(location = value) } },
                                label = "Location *",
                                placeholder = "e.g., Nepal, Himalayas",
                                leadingIcon = Icons.Default.LocationOn,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Hike Date *",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }

                                DarkDateButton(
                                    dateText = vm.draft.date?.toString() ?: "Select date",
                                    hasDate = vm.draft.date != null,
                                    onClick = { showDatePicker = true }
                                )
                            }

                            DarkFormTextField(
                                value = vm.draft.lengthKm,
                                onValueChange = { value ->
                                    if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        vm.update { it.copy(lengthKm = value) }
                                    }
                                },
                                label = "Distance (km) *",
                                placeholder = "e.g., 5.5",
                                leadingIcon = Icons.Default.Straighten,
                                trailingText = "km",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                )
                            )

                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.TrendingUp,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Difficulty Level *",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("Easy", "Moderate", "Hard").forEach { level ->
                                        val selected = vm.draft.difficulty.equals(level, ignoreCase = true)
                                        val (selBg, selFg) = when (level) {
                                            "Easy" -> Color(0xFF66BB6A).copy(alpha = 0.3f) to Color(0xFF81C784)
                                            "Moderate" -> Color(0xFFFFB74D).copy(alpha = 0.3f) to Color(0xFFFFD54F)
                                            "Hard" -> Color(0xFFEF5350).copy(alpha = 0.3f) to Color(0xFFE57373)
                                            else -> Color.White.copy(alpha = 0.1f) to Color.White
                                        }
                                        DarkFilterChip(
                                            selected = selected,
                                            onClick = { vm.update { it.copy(difficulty = level) } },
                                            label = level,
                                            selectedBg = selBg,
                                            selectedFg = selFg,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocalParking,
                                        null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Parking Available? *",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = lightBlue
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    val yesSelected = vm.draft.parking == true
                                    val noSelected = vm.draft.parking == false

                                    DarkFilterChip(
                                        selected = yesSelected,
                                        onClick = { vm.update { it.copy(parking = true) } },
                                        label = "Yes",
                                        icon = if (yesSelected) Icons.Default.Check else Icons.Default.LocalParking,
                                        selectedBg = Color(0xFF66BB6A).copy(alpha = 0.3f),
                                        selectedFg = Color(0xFF81C784),
                                        modifier = Modifier.weight(1f)
                                    )

                                    DarkFilterChip(
                                        selected = noSelected,
                                        onClick = { vm.update { it.copy(parking = false) } },
                                        label = "No",
                                        icon = if (noSelected) Icons.Default.Check else Icons.Default.RemoveCircle,
                                        selectedBg = Color(0xFFEF5350).copy(alpha = 0.3f),
                                        selectedFg = Color(0xFFE57373),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
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

                            DarkFormTextField(
                                value = vm.draft.description,
                                onValueChange = { value -> vm.update { it.copy(description = value) } },
                                label = "Description",
                                placeholder = "Describe your planned hike...",
                                leadingIcon = Icons.AutoMirrored.Filled.Notes,
                                minLines = 3,
                                maxLines = 5,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            DarkFormTextField(
                                value = vm.draft.terrain,
                                onValueChange = { value -> vm.update { it.copy(terrain = value) } },
                                label = "Terrain Type",
                                placeholder = "e.g., Rocky, Forested, Mountain",
                                leadingIcon = Icons.Default.Landscape,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            DarkFormTextField(
                                value = vm.draft.expectedWeather,
                                onValueChange = { value -> vm.update { it.copy(expectedWeather = value) } },
                                label = "Expected Weather",
                                placeholder = "e.g., Sunny, Cloudy, Rainy",
                                leadingIcon = Icons.Default.WbSunny,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
                    }
                }

                
                formError?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                null,
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(lightBlue.copy(alpha = 0.4f))
                        )
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val vr = vm.validate()
                            if (!vr.isValid) {
                                formError = vr.errorMessage ?: "Please fix the form."
                            } else {
                                formError = null
                                showReviewDialog = true
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
                        Icon(Icons.Default.Preview, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Review",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                vm.update { it.copy(date = LocalDate.parse(selectedDate)) }
                showDatePicker = false
            },
            initialDate = vm.draft.date?.toString() ?: ""
        )
    }

    
    if (showReviewDialog) {
        HikeReviewDialog(
            draft = vm.draft,
            isEditMode = isEditMode,
            onDismiss = { showReviewDialog = false },
            onEdit = { showReviewDialog = false },
            onConfirm = {
                vm.saveHike()
                onHikeSaved()
            }
        )
    }
}

@Composable
private fun DarkFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = lightBlue.copy(alpha = 0.8f)
            )
        },
        placeholder = {
            Text(
                placeholder,
                color = Color.White.copy(alpha = 0.4f)
            )
        },
        leadingIcon = {
            Icon(
                leadingIcon,
                null,
                tint = lightBlue,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingText?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = lightBlue.copy(alpha = 0.7f)
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedContainerColor = darkOverlay.copy(alpha = 0.5f),
            unfocusedContainerColor = darkOverlay.copy(alpha = 0.3f),
            focusedBorderColor = Color(0xFF29B6F6),
            unfocusedBorderColor = Color(0xFF29B6F6).copy(alpha = 0.3f),
            cursorColor = Color(0xFF29B6F6)
        )
    )
}

@Composable
private fun DarkDateButton(
    dateText: String,
    hasDate: Boolean,
    onClick: () -> Unit
) {
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = darkOverlay.copy(alpha = 0.4f),
            contentColor = Color.White
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF29B6F6).copy(alpha = 0.3f))
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            Icons.Default.DateRange,
            null,
            modifier = Modifier.size(20.dp),
            tint = lightBlue
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyLarge,
            color = if (hasDate) Color.White else Color.White.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
    }
}

@Composable
private fun DarkFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector? = null,
    selectedBg: Color,
    selectedFg: Color,
    modifier: Modifier = Modifier
) {
    val darkOverlay = Color(0xFF0D1F2D)

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) selectedBg else darkOverlay.copy(alpha = 0.3f),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (selected) selectedFg.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = if (selected) selectedFg else Color.White.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                label,
                fontWeight = FontWeight.Bold,
                color = if (selected) selectedFg else Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun FormBackgroundStars() {
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

@Composable
fun HikeReviewDialog(
    draft: HikeDraft,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onConfirm: () -> Unit
) {
    val darkBg = Color(0xFF0A1929)
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = darkSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        accentBlue,
                                        accentBlue.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isEditMode) "Review Changes" else "Review Your Hike",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                                Text(
                                    text = "Verify all details before saving",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = accentBlue.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(2.dp, accentBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(accentBlue.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Hiking,
                                    null,
                                    tint = lightBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hike Name",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = lightBlue,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = draft.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0D1F2D).copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            Text(
                                text = "Hike Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            ReviewDialogItem(
                                icon = Icons.Default.LocationOn,
                                label = "Location",
                                value = draft.location,
                                iconTint = Color(0xFFFFB74D)
                            )
                            ReviewDialogItem(
                                icon = Icons.Default.DateRange,
                                label = "Date",
                                value = draft.date?.toString() ?: "Not set",
                                iconTint = lightBlue
                            )
                            ReviewDialogItem(
                                icon = Icons.Default.Straighten,
                                label = "Distance",
                                value = "${draft.lengthKm} km",
                                iconTint = accentBlue
                            )

                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.TrendingUp,
                                            null,
                                            modifier = Modifier.size(20.dp),
                                            tint = lightBlue
                                        )
                                    }
                                    Text(
                                        text = "Difficulty",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                val (chipBg, chipFg) = when (draft.difficulty.lowercase()) {
                                    "easy" -> Color(0xFF66BB6A).copy(alpha = 0.3f) to Color(0xFF81C784)
                                    "moderate" -> Color(0xFFFFB74D).copy(alpha = 0.3f) to Color(0xFFFFD54F)
                                    "hard" -> Color(0xFFEF5350).copy(alpha = 0.3f) to Color(0xFFE57373)
                                    else -> Color.White.copy(alpha = 0.1f) to Color.White
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = chipBg
                                ) {
                                    Text(
                                        text = draft.difficulty,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = chipFg,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val parkingBg = if (draft.parking == true)
                                        Color(0xFF66BB6A).copy(alpha = 0.3f)
                                    else
                                        Color(0xFFEF5350).copy(alpha = 0.3f)
                                    val parkingFg = if (draft.parking == true)
                                        Color(0xFF81C784)
                                    else
                                        Color(0xFFE57373)

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(parkingBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            if (draft.parking == true) Icons.Default.LocalParking else Icons.Default.RemoveCircle,
                                            null,
                                            modifier = Modifier.size(20.dp),
                                            tint = parkingFg
                                        )
                                    }
                                    Text(
                                        text = "Parking Available",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = if (draft.parking == true) "Yes" else "No",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (draft.parking == true) Color(0xFF81C784) else Color(0xFFE57373)
                                )
                            }
                        }
                    }

                    
                    if (draft.description.isNotBlank() || draft.terrain.isNotBlank() || draft.expectedWeather.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0D1F2D).copy(alpha = 0.6f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Additional Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                if (draft.description.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.AutoMirrored.Filled.Notes,
                                        label = "Description",
                                        value = draft.description,
                                        iconTint = lightBlue
                                    )
                                }
                                if (draft.terrain.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.Default.Landscape,
                                        label = "Terrain",
                                        value = draft.terrain,
                                        iconTint = accentBlue
                                    )
                                }
                                if (draft.expectedWeather.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.Default.WbSunny,
                                        label = "Expected Weather",
                                        value = draft.expectedWeather,
                                        iconTint = Color(0xFFFFB74D)
                                    )
                                }
                            }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(lightBlue.copy(alpha = 0.4f))
                        )
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isEditMode) "Update" else "Save",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewDialogItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ReviewDialogTextBlock(
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
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0A1929).copy(alpha = 0.5f)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit,
    initialDate: String
) {
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)
    val darkSurface = Color(0xFF1A2F42)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            if (initialDate.isNotEmpty()) {
                val parts = initialDate.split("-")
                if (parts.size == 3) {
                    java.util.Calendar.getInstance().apply {
                        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                    }.timeInMillis
                } else System.currentTimeMillis()
            } else System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = millis }
                        val y = calendar.get(java.util.Calendar.YEAR)
                        val m = calendar.get(java.util.Calendar.MONTH) + 1
                        val d = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        onDateSelected(String.format("%04d-%02d-%02d", y, m, d))
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = accentBlue
                )
            ) {
                Text("OK", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = lightBlue
                )
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = darkSurface,
                titleContentColor = lightBlue,
                headlineContentColor = Color.White,
                weekdayContentColor = lightBlue.copy(alpha = 0.7f),
                dayContentColor = Color.White,
                selectedDayContainerColor = accentBlue,
                selectedDayContentColor = Color.White,
                todayContentColor = accentBlue,
                todayDateBorderColor = accentBlue
            )
        )
    }
}