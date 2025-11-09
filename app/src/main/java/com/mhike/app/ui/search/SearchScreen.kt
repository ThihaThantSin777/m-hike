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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
    val cardBg = Color(0xFF2A4A5E)

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
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Search & Filter",
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
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = lightBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = "Search Filters",
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

                            
                            DarkTextField(
                                value = form.name,
                                onValueChange = { vm.form.value = form.copy(name = it) },
                                label = "Hike Name",
                                placeholder = "Enter hike name...",
                                leadingIcon = Icons.Default.Search,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            
                            DarkTextField(
                                value = form.location,
                                onValueChange = { vm.form.value = form.copy(location = it) },
                                label = "Location",
                                placeholder = "Enter location...",
                                leadingIcon = Icons.Default.LocationOn,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        text = "Distance Range (km)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = lightBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = keyboardOptions,
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
            containerColor = darkOverlay.copy(alpha = 0.4f),
            contentColor = Color.White
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF29B6F6).copy(alpha = 0.3f))
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
                color = lightBlue.copy(alpha = 0.8f)
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasDate)
                    Color.White
                else
                    Color.White.copy(alpha = 0.5f)
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
    val darkOverlay = Color(0xFF0D1F2D)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        )
    ) {
        Box {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7).copy(alpha = 0.6f),
                                Color(0xFF29B6F6).copy(alpha = 0.6f),
                                Color(0xFF03A9F4).copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = hike.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFB74D)
                            )
                            Text(
                                text = hike.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = lightBlue
                            )
                            Text(
                                text = hike.date.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accentBlue.copy(alpha = 0.2f)
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
                                color = lightBlue
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View details",
                        tint = lightBlue,
                        modifier = Modifier.size(22.dp)
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