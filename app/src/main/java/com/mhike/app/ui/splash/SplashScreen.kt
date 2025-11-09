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
                        Color(0xFF0A1929),
                        Color(0xFF1A2F42),
                        Color(0xFF2A4A5E)
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
                        .size(160.dp)
                        .alpha(0.3f)
                        .blur(24.dp)
                ) {
                    MountainIcon(glowMode = true)
                }
                MountainIcon(glowMode = false)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "M-Hike",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.5.sp,
                modifier = Modifier.alpha(alphaAnimation),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(4.dp)
                    .alpha(alphaAnimation)
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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EXPLORE • TRACK • DISCOVER",
                color = Color(0xFF81D4FA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp,
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
                    .width(60.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Adventure Starts Here",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )
        }

        
        MovingMHikeTag(
            progress = pathProgress,
            label = "TRAILBLAZE", 
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 380.dp)
        )
    }
}

@Composable
private fun AnimatedStars(shimmerShift: Float) {
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
            val twinkle = sin(shimmerShift / 100f + position.x + position.y) * 0.5f + 0.5f
            drawCircle(
                color = Color.White.copy(alpha = 0.3f + twinkle * 0.4f),
                radius = baseSize * (0.8f + twinkle * 0.4f),
                center = position
            )
        }
    }
}

@Composable
private fun MovingMHikeTag(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String = "TRAILBLAZE"
) {
    val density = LocalDensity.current

    val boxWidthDp = 280.dp
    val boxHeightDp = 120.dp
    val boxWidthPx = with(density) { boxWidthDp.toPx() }
    val boxHeightPx = with(density) { boxHeightDp.toPx() }

    
    val p0 = Offset(boxWidthPx * 0.1f, boxHeightPx * 0.8f)
    val p1 = Offset(boxWidthPx * 0.5f, boxHeightPx * 0.1f)
    val p2 = Offset(boxWidthPx * 0.9f, boxHeightPx * 0.8f)

    val t = progress.coerceIn(0f, 1f)
    val pos = quadBezier(p0, p1, p2, t)

    val xDp = with(density) { pos.x.toDp() }
    val yDp = with(density) { pos.y.toDp() }

    
    val depthScale = 0.94f + 0.12f * kotlin.math.sin(t * Math.PI).toFloat()

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
                            Color(0xFF0288D1),
                            Color(0xFF0277BD)
                        )
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
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

            drawPath(
                path = path2,
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 2f)
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
