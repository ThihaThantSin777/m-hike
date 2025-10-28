package com.mhike.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.mhike.app.navigation.Destinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, vm: SplashViewModel = hiltViewModel()) {
    var startAnimation by remember { mutableStateOf(false) }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 300
        ),
        label = "alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
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
                        Color(0xFF1565C0),
                        Color(0xFF0D47A1),
                        Color(0xFF01579B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnimation * pulse),
                contentAlignment = Alignment.Center
            ) {
                MountainIcon()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "M-Hike",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alphaAnimation)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explore • Track • Discover",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(alphaAnimation)
            )
        }

        Text(
            text = "Your Adventure Starts Here",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alphaAnimation)
        )
    }
}

@Composable
fun MountainIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

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
            color = Color.White.copy(alpha = 0.9f),
            style = Fill
        )

        drawPath(
            path = path2,
            color = Color.White.copy(alpha = 0.7f),
            style = Fill
        )

        drawPath(
            path = path3,
            color = Color.White.copy(alpha = 0.5f),
            style = Fill
        )

        drawCircle(
            color = Color.White,
            radius = width * 0.12f,
            center = Offset(width * 0.5f, height * 0.15f)
        )
    }
}