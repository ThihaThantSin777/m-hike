package com.mhike.app.ui.hike.list

import androidx.compose.animation.*
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
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.SolidColor
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
        
        AnimatedBackgroundStars()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "M-Hike",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 26.sp,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(3.dp)
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
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${hikes.size} ADVENTURE${if (hikes.size != 1) "S" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = lightBlue,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onOpenSearch,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = lightBlue
                            )
                        }

                        IconButton(onClick = { menuOpen = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White.copy(alpha = 0.8f)
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
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteForever,
                                            contentDescription = null,
                                            tint = Color(0xFFEF5350)
                                        )
                                        Text(
                                            "Reset Database",
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
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
                        containerColor = accentBlue,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "New Hike",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
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
                        .padding(pv)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .blur(30.dp)
                            ) {
                                MiniMountainIcon(glowMode = true)
                            }
                            
                            MiniMountainIcon(glowMode = false)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "No Adventures Yet",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(3.dp)
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

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "EXPLORE • TRACK • DISCOVER",
                                style = MaterialTheme.typography.labelLarge,
                                color = lightBlue,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }

                        Text(
                            text = "Every mountain conquered, every trail explored,\nevery moment captured. Start your journey today.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = onAddClick,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                            contentPadding = PaddingValues(horizontal = 36.dp, vertical = 18.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Begin Your Adventure",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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

    
    val cardBg = Color(0xFF1A2F42)
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
            containerColor = cardBg
        )
    ) {
        Box {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(accentBlue.copy(alpha = 0.15f))
                                    .blur(8.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(accentBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terrain,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = lightBlue
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = hike.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 20.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = lightBlue
                                )
                                Text(
                                    text = hike.date.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = lightBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = accentBlue.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = lightBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = darkOverlay.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFFFFB74D)
                            )
                            Text(
                                text = hike.location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    DarkPill(
                        icon = Icons.Default.Straighten,
                        text = "${hike.lengthKm} km",
                        containerColor = accentBlue.copy(alpha = 0.2f),
                        contentColor = lightBlue
                    )
                }

                
                if (!hike.description.isNullOrEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = darkOverlay.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = hike.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(14.dp),
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                    }
                }

                
                if (hike.difficulty.isNotEmpty() || hike.parking) {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hike.difficulty.isNotEmpty()) {
                            val (bg, fg) = when (hike.difficulty.lowercase()) {
                                "easy" -> Color(0xFF66BB6A).copy(alpha = 0.2f) to Color(0xFF81C784)
                                "moderate" -> Color(0xFFFFB74D).copy(alpha = 0.2f) to Color(0xFFFFD54F)
                                "hard" -> Color(0xFFEF5350).copy(alpha = 0.2f) to Color(0xFFE57373)
                                else -> Color.White.copy(alpha = 0.1f) to Color.White.copy(alpha = 0.8f)
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = bg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = fg
                                    )
                                    Text(
                                        text = hike.difficulty,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = fg
                                    )
                                }
                            }
                        }

                        if (hike.parking) {
                            DarkPill(
                                icon = Icons.Default.LocalParking,
                                text = "Parking",
                                containerColor = Color(0xFF42A5F5).copy(alpha = 0.2f),
                                contentColor = Color(0xFF90CAF9)
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
                        Spacer(Modifier.height(20.dp))

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

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DarkActionButton(
                                icon = Icons.Default.Info,
                                label = "Details",
                                onClick = onCardClick,
                                containerColor = accentBlue.copy(alpha = 0.2f),
                                contentColor = lightBlue,
                                modifier = Modifier.weight(1f)
                            )

                            DarkActionButton(
                                icon = Icons.Default.Visibility,
                                label = "Observations",
                                onClick = onObservationsClick,
                                containerColor = Color(0xFF26A69A).copy(alpha = 0.2f),
                                contentColor = Color(0xFF80CBC4),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DarkActionButton(
                                icon = Icons.Default.Edit,
                                label = "Edit",
                                onClick = onEditClick,
                                containerColor = Color(0xFFAB47BC).copy(alpha = 0.2f),
                                contentColor = Color(0xFFCE93D8),
                                modifier = Modifier.weight(1f)
                            )

                            DarkActionButton(
                                icon = Icons.Default.Delete,
                                label = "Delete",
                                onClick = onDeleteClick,
                                containerColor = Color(0xFFEF5350).copy(alpha = 0.2f),
                                contentColor = Color(0xFFE57373),
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
private fun DarkPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun DarkActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

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