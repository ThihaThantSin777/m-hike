package com.mhike.app.ui.hike.list

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhike.app.domain.model.Hike
import com.mhike.app.ui.components.ConfirmDialog
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeListScreen(
    hikesFlow: StateFlow<List<Hike>>,
    onAddClick: () -> Unit,
    onEditClick: (Hike) -> Unit,
    onDelete: (Hike) -> Unit,
    onResetDatabase: () -> Unit,
    onOpenObservations: (Hike) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDetail: (Hike) -> Unit
) {
    val hikes by hikesFlow.collectAsState()

    var menuOpen by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var hikeToDelete by remember { mutableStateOf<Hike?>(null) }

    
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
        
        AnimatedBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Trail Log",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                letterSpacing = 1.2.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${hikes.size} saved hike${if (hikes.size == 1) "" else "s"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = lightBlue.copy(alpha = 0.9f),
                                letterSpacing = 2.sp
                            )
                        }
                    },
                    actions = {
                        
                        Surface(
                            onClick = onOpenSearch,
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.06f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4FC3F7),
                                        Color(0xFF29B6F6)
                                    )
                                )
                            ),
                            modifier = Modifier
                                .padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = lightBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Search",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = lightBlue
                                )
                            }
                        }

                        IconButton(onClick = { menuOpen = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.background(darkSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteForever,
                                            contentDescription = null,
                                            tint = Color(0xFFEF5350)
                                        )
                                        Column {
                                            Text(
                                                "Reset Database",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White
                                            )
                                            Text(
                                                "Remove all hikes & observations",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    menuOpen = false
                                    showResetConfirm = true
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                if (hikes.isNotEmpty()) {
                    
                    FloatingActionButton(
                        onClick = onAddClick,
                        containerColor = Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF4FC3F7),
                                            Color(0xFF29B6F6),
                                            Color(0xFF03A9F4)
                                        )
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = Color(0xFF021019)
                                )
                                Text(
                                    "New Hike",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF021019),
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { pv ->
            if (hikes.isEmpty()) {
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF102538).copy(alpha = 0.85f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(140.dp)
                                        .blur(24.dp)
                                ) {
                                    MiniMountainIcon(glowMode = true)
                                }
                                MiniMountainIcon(glowMode = false)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No hikes logged yet",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Create your first trail and start recording\nyour journeys, notes, and observations.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.75f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }

                            Button(
                                onClick = onAddClick,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Add your first hike",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    items(items = hikes, key = { it.id }) { hike ->
                        DarkHikeCard(
                            hike = hike,
                            onCardClick = { onOpenDetail(hike) },
                            onObservationsClick = { onOpenObservations(hike) },
                            onEditClick = { onEditClick(hike) },
                            onDeleteClick = {
                                hikeToDelete = hike
                                showDeleteConfirm = true
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    
    if (showResetConfirm) {
        ConfirmDialog(
            title = "Reset Database?",
            message = "This will delete all hikes and observations. This action cannot be undone.",
            confirmText = "Reset",
            onConfirm = {
                onResetDatabase()
                showResetConfirm = false
            },
            onDismiss = { showResetConfirm = false }
        )
    }

    if (showDeleteConfirm && hikeToDelete != null) {
        ConfirmDialog(
            title = "Delete Hike?",
            message = "Are you sure you want to delete \"${hikeToDelete?.name}\"?\n\nThis will also delete all associated observations. This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                hikeToDelete?.let { onDelete(it) }
                showDeleteConfirm = false
                hikeToDelete = null
            },
            onDismiss = {
                showDeleteConfirm = false
                hikeToDelete = null
            }
        )
    }
}


@Composable
fun DarkHikeCard(
    hike: Hike,
    onCardClick: () -> Unit,
    onObservationsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val cardBg = Color(0xFF101F33)
    val accentBlue = Color(0xFF29B6F6)
    val lightBlue = Color(0xFF81D4FA)
    val darkOverlay = Color(0xFF0D1F2D)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardBg.copy(alpha = 0.96f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {

            
            
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = lightBlue
                        )
                        Text(
                            text = hike.date.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = lightBlue.copy(alpha = 0.95f),
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (hike.difficulty.isNotEmpty()) {
                    val (bg, fg) = when (hike.difficulty.lowercase()) {
                        "easy" -> Color(0xFF66BB6A).copy(alpha = 0.18f) to Color(0xFF81C784)
                        "moderate" -> Color(0xFFFFB74D).copy(alpha = 0.18f) to Color(0xFFFFD54F)
                        "hard" -> Color(0xFFEF5350).copy(alpha = 0.18f) to Color(0xFFE57373)
                        else -> Color.White.copy(alpha = 0.12f) to Color.White.copy(alpha = 0.9f)
                    }

                    DarkPill(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        text = hike.difficulty,
                        containerColor = bg,
                        contentColor = fg
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            
            
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.06f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = null,
                            tint = lightBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = hike.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            
            
            
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DarkPill(
                    icon = Icons.Default.Straighten,
                    text = "${hike.lengthKm} km",
                    containerColor = accentBlue.copy(alpha = 0.18f),
                    contentColor = lightBlue
                )

                if (hike.parking) {
                    Spacer(Modifier.width(8.dp))
                    DarkPill(
                        icon = Icons.Default.LocalParking,
                        text = "Parking",
                        containerColor = Color(0xFF42A5F5).copy(alpha = 0.2f),
                        contentColor = Color(0xFF90CAF9)
                    )
                }
            }

            
            
            
            if (!hike.description.isNullOrEmpty()) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = darkOverlay.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = hike.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.86f),
                        modifier = Modifier.padding(12.dp),
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }

            
            
            
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                Text(
                    text = if (expanded) "Tap to collapse" else "Tap to see actions",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = lightBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            
            
            
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        lightBlue.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Spacer(Modifier.height(8.dp))

                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick actions",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Tap an action below",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconActionChip(
                            icon = Icons.Default.Info,
                            label = "Details",
                            containerColor = accentBlue.copy(alpha = 0.18f),
                            contentColor = lightBlue,
                            onClick = onCardClick,
                            modifier = Modifier.weight(1f)
                        )

                        IconActionChip(
                            icon = Icons.Default.Visibility,
                            label = "Observations",
                            containerColor = Color(0xFF26A69A).copy(alpha = 0.18f),
                            contentColor = Color(0xFF80CBC4),
                            onClick = onObservationsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconActionChip(
                            icon = Icons.Default.Edit,
                            label = "Edit",
                            containerColor = Color(0xFFAB47BC).copy(alpha = 0.18f),
                            contentColor = Color(0xFFCE93D8),
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        )

                        IconActionChip(
                            icon = Icons.Default.Delete,
                            label = "Delete",
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.18f),
                            contentColor = Color(0xFFE57373),
                            onClick = onDeleteClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun DarkPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}



/* ----------------------------------------------------------------
   BACKGROUND COMPONENTS – left EXACTLY as you wrote them
   ---------------------------------------------------------------- */

@Composable
private fun AnimatedBackgroundStars() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween (2000, easing = androidx.compose.animation.core.LinearEasing)
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

@Composable
private fun MiniMountainIcon(glowMode: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (glowMode) {
            val glowPath = Path().apply {
                moveTo(0f, height)
                lineTo(width * 0.35f, height * 0.2f)
                lineTo(width * 0.6f, height)
                close()

                moveTo(width * 0.25f, height)
                lineTo(width * 0.5f, height * 0.35f)
                lineTo(width * 0.75f, height)
                close()

                moveTo(width * 0.4f, height)
                lineTo(width * 0.65f, height * 0.25f)
                lineTo(width, height)
                close()
            }
            drawPath(path = glowPath, color = Color(0xFF29B6F6), style = Fill)
        } else {
            val path1 = Path().apply {
                moveTo(0f, height)
                lineTo(width * 0.35f, height * 0.2f)
                lineTo(width * 0.6f, height)
                close()
            }

            val path2 = Path().apply {
                moveTo(width * 0.25f, height)
                lineTo(width * 0.5f, height * 0.35f)
                lineTo(width * 0.75f, height)
                close()
            }

            val path3 = Path().apply {
                moveTo(width * 0.4f, height)
                lineTo(width * 0.65f, height * 0.25f)
                lineTo(width, height)
                close()
            }

            drawPath(
                path = path1,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFB3E5FC)
                    )
                ),
                style = Fill
            )
            drawPath(
                path = path2,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF81D4FA),
                        Color(0xFF4FC3F7)
                    )
                ),
                style = Fill
            )
            drawPath(
                path = path3,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF29B6F6),
                        Color(0xFF03A9F4)
                    )
                ),
                style = Fill
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4),
                        Color(0xFFFFF59D),
                        Color(0xFFFFF176)
                    )
                ),
                radius = width * 0.13f,
                center = Offset(width * 0.5f, height * 0.15f)
            )

            drawCircle(
                color = Color(0xFFFFF176).copy(alpha = 0.3f),
                radius = width * 0.17f,
                center = Offset(width * 0.5f, height * 0.15f)
            )
        }
    }
}

@Composable
private fun IconActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor.copy(alpha = 0.9f),
        border = BorderStroke(
            width = 1.dp,
            color = contentColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.18f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
            }

            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

