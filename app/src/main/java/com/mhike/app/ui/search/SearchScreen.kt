package com.mhike.app.ui.search

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhike.app.domain.model.Hike
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onTapSearchResult: (Hike) -> Unit,
    vm: SearchViewModel = hiltViewModel()
) {
    val form by vm.form.collectAsState()
    val results by vm.results.collectAsState()

    val darkBg = Color(0xFF0A1929)
    val darkSurface = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

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
        
        SearchBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                "Search Results",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                letterSpacing = 0.5.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Tap a hike to view details",
                                style = MaterialTheme.typography.labelMedium,
                                color = lightBlue.copy(alpha = 0.8f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.18f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                
                
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = darkSurface.copy(alpha = 0.96f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
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
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF243447),
                                                    Color(0xFF1A2F42)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Search Filters",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Fine-tune your next adventure",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = lightBlue.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = lightBlue.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
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

                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Hike & Location",
                                style = MaterialTheme.typography.labelLarge,
                                color = lightBlue,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DarkTextField(
                                    value = form.name,
                                    onValueChange = { vm.form.value = form.copy(name = it) },
                                    label = "Hike Name",
                                    placeholder = "e.g. Skyline Trail",
                                    leadingIcon = Icons.Default.Search,
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                DarkTextField(
                                    value = form.location,
                                    onValueChange = { vm.form.value = form.copy(location = it) },
                                    label = "Location",
                                    placeholder = "e.g. National Park",
                                    leadingIcon = Icons.Default.LocationOn,
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    )
                                )
                            }
                        }

                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Straighten,
                                    contentDescription = null,
                                    tint = lightBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Distance (km)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = lightBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DarkTextField(
                                    value = form.minLen,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            vm.form.value = form.copy(minLen = newValue)
                                        }
                                    },
                                    label = "Min",
                                    placeholder = "0",
                                    leadingIcon = Icons.Default.Remove,
                                    trailingText = "km",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next
                                    ),
                                    isError = form.minLen.isNotEmpty() && !form.minLen.matches(Regex("^\\d*\\.?\\d*$"))
                                )

                                DarkTextField(
                                    value = form.maxLen,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            vm.form.value = form.copy(maxLen = newValue)
                                        }
                                    },
                                    label = "Max",
                                    placeholder = "100",
                                    leadingIcon = Icons.Default.Add,
                                    trailingText = "km",
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    isError = form.maxLen.isNotEmpty() && !form.maxLen.matches(Regex("^\\d*\\.?\\d*$"))
                                )
                            }
                        }

                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = lightBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Date Range",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = lightBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            var showStartDatePicker by remember { mutableStateOf(false) }
                            var showEndDatePicker by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DarkDateButton(
                                    label = "From",
                                    dateText = form.startDate.ifEmpty { "Select date" },
                                    hasDate = form.startDate.isNotEmpty(),
                                    onClick = { showStartDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                )

                                DarkDateButton(
                                    label = "To",
                                    dateText = form.endDate.ifEmpty { "Select date" },
                                    hasDate = form.endDate.isNotEmpty(),
                                    onClick = { showEndDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (showStartDatePicker) {
                                AppDatePickerDialog(
                                    onDismissRequest = { showStartDatePicker = false },
                                    onDateSelected = { selectedDate ->
                                        vm.form.value = form.copy(startDate = selectedDate)
                                        showStartDatePicker = false
                                    },
                                    initialDate = form.startDate
                                )
                            }

                            if (showEndDatePicker) {
                                AppDatePickerDialog(
                                    onDismissRequest = { showEndDatePicker = false },
                                    onDateSelected = { selectedDate ->
                                        vm.form.value = form.copy(endDate = selectedDate)
                                        showEndDatePicker = false
                                    },
                                    initialDate = form.endDate
                                )
                            }
                        }
                    }
                }

                
                
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = lightBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Search Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (results.isEmpty())
                                    "No matching hikes yet"
                                else
                                    "Tap a hike to view details",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = accentBlue.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "${results.size} found",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = lightBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                
                
                
                if (results.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .blur(20.dp)
                                        .clip(CircleShape)
                                        .background(accentBlue.copy(alpha = 0.3f))
                                )
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = lightBlue.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "No hikes found",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Try adjusting your search filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(results) { hike ->
                            DarkHikeResultCard(
                                hike = hike,
                                onClick = { onTapSearchResult(hike) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = lightBlue.copy(alpha = 0.85f)
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
                    color = lightBlue.copy(alpha = 0.75f)
                )
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = keyboardOptions,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedContainerColor = darkOverlay.copy(alpha = 0.55f),
            unfocusedContainerColor = darkOverlay.copy(alpha = 0.35f),
            focusedBorderColor = Color(0xFF29B6F6),
            unfocusedBorderColor = Color(0xFF29B6F6).copy(alpha = 0.35f),
            cursorColor = Color(0xFF29B6F6),
            errorBorderColor = Color(0xFFEF5350),
            errorContainerColor = darkOverlay.copy(alpha = 0.5f)
        )
    )
}

@Composable
private fun DarkDateButton(
    label: String,
    dateText: String,
    hasDate: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = darkOverlay.copy(alpha = 0.45f),
            contentColor = Color.White
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(Color(0xFF29B6F6).copy(alpha = 0.35f))
        )
    ) {
        Icon(
            Icons.Default.DateRange,
            null,
            modifier = Modifier.size(20.dp),
            tint = lightBlue
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = lightBlue.copy(alpha = 0.85f)
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasDate)
                    Color.White
                else
                    Color.White.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
fun DarkHikeResultCard(
    hike: Hike,
    onClick: () -> Unit
) {
    val cardBg = Color(0xFF1A2F42)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg.copy(alpha = 0.96f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF243447),
                                Color(0xFF1A2F42)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terrain,
                    contentDescription = null,
                    tint = lightBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = hike.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFB74D)
                    )
                    Text(
                        text = hike.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = lightBlue
                    )
                    Text(
                        text = hike.date.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = accentBlue.copy(alpha = 0.22f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = lightBlue
                        )
                        Text(
                            text = "${hike.lengthKm} km",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = lightBlue,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View details",
                        tint = lightBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBackgroundStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.LinearEasing)
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
            Offset(width * 0.1f, height * 0.7f) to 2.5f,
            Offset(width * 0.5f, height * 0.25f) to 1.8f,
            Offset(width * 0.65f, height * 0.75f) to 2.2f
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

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit,
    initialDate: String
) {
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            if (initialDate.isNotEmpty()) {
                val parts = initialDate.split("-")
                if (parts.size == 3) {
                    java.util.Calendar.getInstance().apply {
                        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                    }.timeInMillis
                } else {
                    System.currentTimeMillis()
                }
            } else {
                System.currentTimeMillis()
            }
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
                        val year = calendar.get(java.util.Calendar.YEAR)
                        val month = calendar.get(java.util.Calendar.MONTH) + 1
                        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        onDateSelected(String.format("%04d-%02d-%02d", year, month, day))
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = accentBlue
                )
            ) {
                Text(
                    "OK",
                    fontWeight = FontWeight.Bold
                )
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
                containerColor = Color(0xFF1A2F42),
                titleContentColor = lightBlue,
                headlineContentColor = Color.White,
                weekdayContentColor = lightBlue.copy(alpha = 0.7f),
                dayContentColor = Color.White,
                selectedDayContainerColor = accentBlue,
                selectedDayContentColor = Color.White,
                todayContentColor = accentBlue,
                todayDateBorderColor = accentBlue,
                dayInSelectionRangeContentColor = Color.White,
                dayInSelectionRangeContainerColor = accentBlue.copy(alpha = 0.3f)
            )
        )
    }
}
