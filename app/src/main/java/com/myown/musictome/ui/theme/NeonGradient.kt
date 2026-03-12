package com.myown.musictome.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.animation.core.RepeatMode

@Composable
fun neonGradient(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    return Brush.linearGradient(
        colors = listOf(NeonGreen, NeonBlue, NeonPink, DeepBlack,ElectricBlue,IceBlue,CyberMagenta,Ultraviolet, GasOrange,LaserYellow),
        start = Offset(offset, offset),
        end = Offset(offset + 500f, offset + 500f),
        tileMode = TileMode.Mirror
    )
}