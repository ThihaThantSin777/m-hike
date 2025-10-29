package com.mhike.app.ui.hike.form

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeFormScreen(
    onHikeSaved: () -> Unit,
    onBack: () -> Unit,
    vm: HikeFormViewModel = hiltViewModel()
) {
    var formError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "New Hike",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
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
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1565C0)
                    )
                    Column {
                        Text(
                            text = "Create New Hike",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fill in the details below. Fields marked with * are required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Basic Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(thickness = 1.dp)

                    OutlinedTextField(
                        value = vm.draft.name,
                        onValueChange = { value -> vm.update { it.copy(name = value) } },
                        label = { Text("Hike Name *") },
                        placeholder = { Text("e.g., Mount Everest Summit") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Hiking,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )

                    OutlinedTextField(
                        value = vm.draft.location,
                        onValueChange = { value -> vm.update { it.copy(location = value) } },
                        label = { Text("Location *") },
                        placeholder = { Text("e.g., Nepal, Himalayas") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Hike Date *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = if (vm.draft.date != null)
                                        vm.draft.date.toString()
                                    else
                                        "Select date",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (vm.draft.date != null)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = vm.draft.lengthKm,
                        onValueChange = { value ->
                            if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                                vm.update { it.copy(lengthKm = value) }
                            }
                        },
                        label = { Text("Distance (km) *") },
                        placeholder = { Text("e.g., 5.5") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "km",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Difficulty Level *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Easy", "Moderate", "Hard").forEach { level ->
                                FilterChip(
                                    selected = vm.draft.difficulty.equals(level, ignoreCase = true),
                                    onClick = { vm.update { it.copy(difficulty = level) } },
                                    label = { Text(level) },
                                    leadingIcon = if (vm.draft.difficulty.equals(level, ignoreCase = true)) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null,
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (level) {
                                            "Easy" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                            "Moderate" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                            "Hard" -> Color(0xFFF44336).copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.secondaryContainer
                                        },
                                        selectedLabelColor = when (level) {
                                            "Easy" -> Color(0xFF2E7D32)
                                            "Moderate" -> Color(0xFFE65100)
                                            "Hard" -> Color(0xFFC62828)
                                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Parking Available? *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = vm.draft.parking == true,
                                onClick = { vm.update { it.copy(parking = true) } },
                                label = { Text("Yes") },
                                leadingIcon = if (vm.draft.parking == true) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.LocalParking,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF2E7D32)
                                )
                            )
                            FilterChip(
                                selected = vm.draft.parking == false,
                                onClick = { vm.update { it.copy(parking = false) } },
                                label = { Text("No") },
                                leadingIcon = if (vm.draft.parking == false) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.RemoveCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF44336).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFC62828)
                                )
                            )
                        }
                    }
                }
            }

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

                    OutlinedTextField(
                        value = vm.draft.description,
                        onValueChange = { value -> vm.update { it.copy(description = value) } },
                        label = { Text("Description") },
                        placeholder = { Text("Describe your planned hike...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )

                    OutlinedTextField(
                        value = vm.draft.terrain,
                        onValueChange = { value -> vm.update { it.copy(terrain = value) } },
                        label = { Text("Terrain Type") },
                        placeholder = { Text("e.g., Rocky, Forested, Mountain") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Landscape,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )

                    OutlinedTextField(
                        value = vm.draft.expectedWeather,
                        onValueChange = { value -> vm.update { it.copy(expectedWeather = value) } },
                        label = { Text("Expected Weather") },
                        placeholder = { Text("e.g., Sunny, Cloudy, Rainy") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor = Color(0xFF1565C0),
                            focusedLeadingIconColor = Color(0xFF1565C0)
                        )
                    )
                }
            }

            formError?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cancel",
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Preview,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Review",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Date Picker Dialog
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

    // Review Dialog
    if (showReviewDialog) {
        HikeReviewDialog(
            draft = vm.draft,
            onDismiss = { showReviewDialog = false },
            onEdit = { showReviewDialog = false },
            onConfirm = {
                vm.saveHike()
                showReviewDialog = false
                onHikeSaved()
            }
        )
    }
}

@Composable
fun HikeReviewDialog(
    draft: HikeDraft,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog (
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
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1565C0),
                    tonalElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = "Review Your Hike",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Please verify all details",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hike Name - Prominent Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1565C0).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF1565C0).copy(alpha = 0.3f))
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
                                color = Color(0xFF1565C0).copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Hiking,
                                        contentDescription = null,
                                        tint = Color(0xFF1565C0),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hike Name",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = draft.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }

                    // Main Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = "Hike Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Location
                            ReviewDialogItem(
                                icon = Icons.Default.LocationOn,
                                label = "Location",
                                value = draft.location,
                                iconTint = Color(0xFF26A69A)
                            )

                            // Date
                            ReviewDialogItem(
                                icon = Icons.Default.DateRange,
                                label = "Date",
                                value = draft.date?.toString() ?: "Not set",
                                iconTint = Color(0xFF1565C0)
                            )

                            // Distance
                            ReviewDialogItem(
                                icon = Icons.Default.Straighten,
                                label = "Distance",
                                value = "${draft.lengthKm} km",
                                iconTint = Color(0xFFFF9800)
                            )

                            // Difficulty with badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF7E57C2).copy(alpha = 0.15f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = Color(0xFF7E57C2)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Difficulty",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (draft.difficulty.lowercase()) {
                                        "easy" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                        "moderate" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                        "hard" -> Color(0xFFF44336).copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Text(
                                        text = draft.difficulty,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = when (draft.difficulty.lowercase()) {
                                            "easy" -> Color(0xFF2E7D32)
                                            "moderate" -> Color(0xFFE65100)
                                            "hard" -> Color(0xFFC62828)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            // Parking
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (draft.parking == true)
                                            Color(0xFF4CAF50).copy(alpha = 0.15f)
                                        else
                                            Color(0xFFF44336).copy(alpha = 0.15f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (draft.parking == true)
                                                    Icons.Default.LocalParking
                                                else
                                                    Icons.Default.RemoveCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = if (draft.parking == true)
                                                    Color(0xFF4CAF50)
                                                else
                                                    Color(0xFFF44336)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Parking Available",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (draft.parking == true) "Yes" else "No",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (draft.parking == true)
                                        Color(0xFF4CAF50)
                                    else
                                        Color(0xFFF44336)
                                )
                            }
                        }
                    }

                    // Additional Information (if any)
                    if (draft.description.isNotBlank() || draft.terrain.isNotBlank() || draft.expectedWeather.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (draft.description.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.AutoMirrored.Filled.Notes,
                                        label = "Description",
                                        value = draft.description,
                                        iconTint = Color(0xFF1565C0)
                                    )
                                }

                                if (draft.terrain.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.Default.Landscape,
                                        label = "Terrain",
                                        value = draft.terrain,
                                        iconTint = Color(0xFF8D6E63)
                                    )
                                }

                                if (draft.expectedWeather.isNotBlank()) {
                                    ReviewDialogTextBlock(
                                        icon = Icons.Default.WbSunny,
                                        label = "Expected Weather",
                                        value = draft.expectedWeather,
                                        iconTint = Color(0xFFFFA726)
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons (Sticky at bottom)
                HorizontalDivider(thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Edit",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Confirm & Save",
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
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ReviewDialogTextBlock(
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
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
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

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit,
    initialDate: String
) {
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
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = java.util.Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val year = calendar.get(java.util.Calendar.YEAR)
                        val month = calendar.get(java.util.Calendar.MONTH) + 1
                        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
                        onDateSelected(formattedDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = Color(0xFF1565C0),
                headlineContentColor = Color(0xFF1565C0),
                selectedDayContainerColor = Color(0xFF1565C0),
                todayContentColor = Color(0xFF1565C0),
                todayDateBorderColor = Color(0xFF1565C0)
            )
        )
    }
}