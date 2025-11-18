package com.mhike.app.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.mhike.app.navigation.Destinations
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun SplashScreen(
    navController: NavController,
    vm: SplashViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }

    
    val scaleAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale-in"
    )

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 900,
            delayMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "fade"
    )

    val infinite = rememberInfiniteTransition(label = "infinite")

    val pulse by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val shimmerShift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            )
        ),
        label = "shimmer"
    )

    val pathProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "path"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(4000)
        navController.navigate(Destinations.HikeList.route) {
            popUpTo(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020616),
                        Color(0xFF071A2C),
                        Color(0xFF042F3E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        
        AnimatedStars(shimmerShift = shimmerShift)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scaleAnimation * pulse),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .alpha(0.4f)
                        .blur(30.dp)
                ) {
                    MountainIcon(glowMode = true)
                }
                MountainIcon(glowMode = false)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "M-Hike",
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE0F7FA),
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(alphaAnimation),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(5.dp)
                    .alpha(alphaAnimation)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00E5FF),
                                Color(0xFF1DE9B6),
                                Color(0xFF00E676)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "MAP • TRAILS • ELEVATION",
                color = Color(0xFF80DEEA),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(alphaAnimation),
                textAlign = TextAlign.Center
            )
        }

        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .alpha(alphaAnimation)
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width(80.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Navigate Every Step With Confidence",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        }

        
        MovingMHikeTag(
            progress = pathProgress,
            label = "LIVE TRAIL STATUS",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 360.dp)
        )
    }
}

@Composable
private fun AnimatedStars(shimmerShift: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        
        val auroraPath = Path().apply {
            moveTo(width * 0.1f, height)
            cubicTo(
                width * 0.25f, height * 0.6f,
                width * 0.05f, height * 0.4f,
                width * 0.3f, height * 0.1f
            )
            lineTo(width * 0.45f, 0f)
            lineTo(width * 0.15f, 0f)
            close()
        }

        drawPath(
            path = auroraPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x2200E5FF),
                    Color(0x4400E676),
                    Color.Transparent
                )
            ),
            style = Fill
        )

        
        val orbs = listOf(
            Offset(width * 0.2f, height * 0.25f) to 32f,
            Offset(width * 0.8f, height * 0.2f) to 26f,
            Offset(width * 0.25f, height * 0.6f) to 24f,
            Offset(width * 0.7f, height * 0.7f) to 30f,
            Offset(width * 0.5f, height * 0.35f) to 20f
        )

        orbs.forEachIndexed { index, (center, baseRadius) ->
            val phase = shimmerShift / 80f + index * 0.8f
            val pulse = sin(phase) * 0.5f + 0.5f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66B2EBF2),
                        Color.Transparent
                    )
                ),
                radius = baseRadius * (1.2f + pulse * 0.5f),
                center = Offset(
                    x = center.x + sin(phase) * 10f,
                    y = center.y + sin(phase * 1.3f) * 8f
                )
            )
        }

        
        val stars = listOf(
            Offset(width * 0.15f, height * 0.18f),
            Offset(width * 0.9f, height * 0.12f),
            Offset(width * 0.3f, height * 0.32f),
            Offset(width * 0.75f, height * 0.42f),
            Offset(width * 0.5f, height * 0.18f),
            Offset(width * 0.1f, height * 0.55f)
        )

        stars.forEachIndexed { index, position ->
            val twinkle = sin(shimmerShift / 90f + index) * 0.5f + 0.5f
            drawCircle(
                color = Color.White.copy(alpha = 0.35f + twinkle * 0.4f),
                radius = 1.2f + twinkle * 1.5f,
                center = position
            )
        }
    }
}

@Composable
private fun MovingMHikeTag(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String = "LIVE TRAIL STATUS"
) {
    val density = LocalDensity.current

    val boxWidthDp = 280.dp
    val boxHeightDp = 120.dp
    val boxWidthPx = with(density) { boxWidthDp.toPx() }
    val boxHeightPx = with(density) { boxHeightDp.toPx() }

    
    val p0 = Offset(boxWidthPx * 0.1f, boxHeightPx * 0.85f)
    val p1 = Offset(boxWidthPx * 0.5f, boxHeightPx * 0.25f)
    val p2 = Offset(boxWidthPx * 0.9f, boxHeightPx * 0.85f)

    val t = progress.coerceIn(0f, 1f)
    val pos = quadBezier(p0, p1, p2, t)

    val xDp = with(density) { pos.x.toDp() }
    val yDp = with(density) { pos.y.toDp() }

    val depthScale = 0.92f + 0.14f * sin(t * Math.PI).toFloat()

    Box(
        modifier = modifier
            .size(boxWidthDp, boxHeightDp)
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(xDp.roundToPx(), yDp.roundToPx()) }
                .scale(depthScale)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x3300E5FF),
                            Color(0x3300E676)
                        )
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF00BFA5)
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = label,
                    color = Color(0xFFE0F7FA),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )
            }
        }
    }
}

private fun quadBezier(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val u = 1f - t
    val x = u.pow(2) * p0.x + 2f * u * t * p1.x + t.pow(2) * p2.x
    val y = u.pow(2) * p0.y + 2f * u * t * p1.y + t.pow(2) * p2.y
    return Offset(x, y)
}

@Composable
fun MountainIcon(glowMode: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val radius = width * 0.35f

        if (glowMode) {
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x4400E5FF),
                        Color.Transparent
                    )
                ),
                radius = radius * 1.8f,
                center = center
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3300E676),
                        Color.Transparent
                    )
                ),
                radius = radius * 1.4f,
                center = center
            )
        } else {
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D192B),
                        Color(0xFF021018)
                    )
                ),
                radius = radius,
                center = center
            )

            
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF1DE9B6),
                        Color(0xFF00E676),
                        Color(0xFF00E5FF)
                    )
                ),
                radius = radius * 0.98f,
                center = center,
                style = Stroke(width = radius * 0.12f)
            )

            
            drawCircle(
                color = Color(0x3300E5FF),
                radius = radius * 0.55f,
                center = center,
                style = Stroke(width = radius * 0.04f)
            )

            
            val mountainPath = Path().apply {
                moveTo(center.x - radius * 0.55f, center.y + radius * 0.25f)
                lineTo(center.x - radius * 0.15f, center.y - radius * 0.35f)
                lineTo(center.x + radius * 0.05f, center.y - radius * 0.05f)
                lineTo(center.x + radius * 0.3f, center.y - radius * 0.25f)
                lineTo(center.x + radius * 0.5f, center.y + radius * 0.22f)
                close()
            }

            drawPath(
                path = mountainPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2EBF2),
                        Color(0xFF4DD0E1)
                    )
                ),
                style = Fill
            )

            
            val pathLine = Path().apply {
                moveTo(center.x - radius * 0.45f, center.y + radius * 0.18f)
                cubicTo(
                    center.x - radius * 0.2f, center.y + radius * 0.05f,
                    center.x + radius * 0.1f, center.y + radius * 0.05f,
                    center.x + radius * 0.35f, center.y - radius * 0.08f
                )
            }

            drawPath(
                path = pathLine,
                color = Color(0xFF00E5FF),
                style = Stroke(width = radius * 0.045f)
            )

            
            drawCircle(
                color = Color(0xFFFFF59D),
                radius = radius * 0.07f,
                center = Offset(
                    center.x + radius * 0.25f,
                    center.y - radius * 0.12f
                )
            )
        }
    }
}
