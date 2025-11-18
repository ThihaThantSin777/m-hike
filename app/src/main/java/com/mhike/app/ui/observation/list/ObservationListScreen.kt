package com.mhike.app.ui.observation.list

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhike.app.domain.model.Observation
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationListScreen(
    hikeId: Long,
    hikeName: String,
    onAdd: () -> Unit,
    onEdit: (obsId: Long) -> Unit,
    onBack: () -> Unit,
    vm: ObservationListViewModel = hiltViewModel()
) {
    
    val deepNavy = Color(0xFF0A1628)
    val richBlue = Color(0xFF1A2942)
    val accentCyan = Color(0xFF00D9FF)
    val accentTeal = Color(0xFF00FFD1)

    val observations by remember(hikeId) { vm.state(hikeId) }.collectAsState()
    var observationToDelete by remember { mutableStateOf<Observation?>(null) }

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
        
        EnhancedObservationBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ModernObservationTopBar(
                    hikeName = hikeName,
                    observationCount = observations.size,
                    onBack = onBack
                )
            },
            floatingActionButton = {
                AnimatedObservationFAB(
                    visible = observations.isNotEmpty(),
                    onClick = onAdd
                )
            }
        ) { paddingValues ->
            if (observations.isEmpty()) {
                EmptyObservationState(
                    onAdd = onAdd,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    items(items = observations, key = { it.id }) { observation ->
                        ModernObservationCard(
                            observation = observation,
                            onEdit = { onEdit(observation.id) },
                            onDelete = { observationToDelete = observation }
                        )
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }

        
        if (observationToDelete != null) {
            ModernDeleteDialog(
                onDismiss = { observationToDelete = null },
                onConfirm = {
                    observationToDelete?.let { vm.onDelete(it) }
                    observationToDelete = null
                }
            )
        }
    }
}

/* ============================================================================
   TOP BAR
   ============================================================================ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernObservationTopBar(
    hikeName: String,
    observationCount: Int,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = hikeName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "$observationCount ${if (observationCount == 1) "observation" else "observations"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00FFD1),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
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
   FAB
   ============================================================================ */

@Composable
private fun AnimatedObservationFAB(
    visible: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )

    val rotation by rememberInfiniteTransition(label = "fab_rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    if (visible) {
        Box(modifier = Modifier.scale(scale)) {
            
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .rotate(rotation)
                    .blur(20.dp)
                    .align(Alignment.Center)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D9FF).copy(alpha = 0.4f),
                                Color(0xFF00FFD1).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }

            FloatingActionButton(
                onClick = onClick,
                modifier = Modifier.size(64.dp),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF00FFD1)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Observation",
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF0A1628)
                    )
                }
            }
        }
    }
}

/* ============================================================================
   EMPTY STATE
   ============================================================================ */

@Composable
private fun EmptyObservationState(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .blur(40.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF).copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00D9FF).copy(alpha = 0.2f),
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = Color(0xFF00D9FF)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No Observations Yet",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Start documenting your hike discoveries.\nCapture interesting moments and details along the way.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Button(
                onClick = onAdd,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 220.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
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
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF0A1628)
                        )
                        Text(
                            "Add First Observation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0A1628)
                        )
                    }
                }
            }
        }
    }
}

/* ============================================================================
   OBSERVATION CARD
   ============================================================================ */

@Composable
private fun ModernObservationCard(
    observation: Observation,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val cardElevation by animateDpAsState(
        targetValue = if (expanded) 12.dp else 6.dp,
        label = "card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF15223A).copy(alpha = 0.95f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            
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
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00D9FF).copy(alpha = 0.25f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.RemoveRedEye,
                                    contentDescription = null,
                                    tint = Color(0xFF00D9FF),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = formatDateTime(observation.at),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatTime(observation.at),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = Color(0xFF00FFD1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                
                Text(
                    text = observation.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )

                
                if (!observation.comment.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0D1B2E).copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00FFD1).copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Comment,
                                        contentDescription = null,
                                        tint = Color(0xFF00FFD1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Additional Notes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF00FFD1).copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = observation.comment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF00D9FF).copy(alpha = 0.3f),
                                            Color(0xFF00FFD1).copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                icon = Icons.Default.Edit,
                                label = "Edit",
                                onClick = onEdit,
                                color = Color(0xFF00D9FF),
                                modifier = Modifier.weight(1f)
                            )

                            ActionButton(
                                icon = Icons.Default.Delete,
                                label = "Delete",
                                onClick = onDelete,
                                color = Color(0xFFEF5350),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = color,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/* ============================================================================
   DELETE DIALOG
   ============================================================================ */

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
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Delete Observation?",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 22.sp
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this observation? This action cannot be undone.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
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
private fun formatDateTime(instant: Instant): String {
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${months[ldt.month.number - 1]} ${ldt.day}, ${ldt.year}"
}

@OptIn(ExperimentalTime::class)
@SuppressLint("DefaultLocale")
private fun formatTime(instant: Instant): String {
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = ldt.hour
    val minute = ldt.minute
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}

/* ============================================================================
   BACKGROUND ANIMATIONS
   ============================================================================ */

@Composable
private fun EnhancedObservationBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingObservationParticles()
        AnimatedObservationOrbs()
    }
}

@Composable
private fun FloatingObservationParticles() {
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

        for (i in 0..12) {
            val baseX = width * (i / 12f)
            val baseY = (offset1 + i * 80) % height
            val sineOffset = sin((offset1 + i * 50) / 100f) * 30f

            drawCircle(
                color = Color(0xFF00D9FF).copy(alpha = 0.1f),
                radius = 2f + (i % 3),
                center = Offset(baseX + sineOffset, baseY)
            )
        }
    }
}

@Composable
private fun AnimatedObservationOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing)
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
                center = Offset(width * 0.85f, height * 0.15f)
            ),
            radius = 200f,
            center = Offset(
                width * 0.85f + cos(Math.toRadians(rotation.toDouble())).toFloat() * 30f,
                height * 0.15f + sin(Math.toRadians(rotation.toDouble())).toFloat() * 30f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00FFD1).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(width * 0.15f, height * 0.75f)
            ),
            radius = 150f,
            center = Offset(
                width * 0.15f + sin(Math.toRadians(rotation.toDouble())).toFloat() * 25f,
                height * 0.75f + cos(Math.toRadians(rotation.toDouble())).toFloat() * 25f
            )
        )
    }
}