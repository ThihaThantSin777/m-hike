package com.mhike.app.theme


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: Float = 4f,
    val sm: Float = 8f,
    val md: Float = 12f,
    val lg: Float = 16f,
    val xl: Float = 24f,
    val xxl: Float = 32f
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val ScreenPadding = 16.dp
