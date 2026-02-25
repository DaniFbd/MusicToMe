package com.myown.musictome.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
)
val AmoledDarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color.Black,
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White
)

// 2. Tema OCEANO (Azules profundos)
val OceanDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFF00B0FF),
    background = Color(0xFF001219),
    surface = Color(0xFF001E26),
    onBackground = Color(0xFFE0FBFC)
)

// 3. Tema BOSQUE (Elegante y relajante)
val ForestDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF4CAF50),
    background = Color(0xFF0A1A0A),
    surface = Color(0xFF142614),
    onBackground = Color(0xFFE8F5E9)
)

// 4. Tema RETRO AMBAR (Como los equipos de música clásicos)
val RetroDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB300),
    secondary = Color(0xFFFF8F00),
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF242424),
    onBackground = Color(0xFFFFECB3)
)