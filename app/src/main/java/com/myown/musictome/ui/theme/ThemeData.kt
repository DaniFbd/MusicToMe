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

// 5. Tema NEON un experimento de colores vivos
val NeonColorScheme = darkColorScheme(
    //Afecta a titulo de la cancion del BottomPlayerBar, boton play, shuffle, repeat del reproductor
    //titulo de las listas, boton cancelar del dialog añadir a la lista, el color de fondo del checkbox
    primary = NeonGreen,
    secondary = NeonBlue,
    background = DeepBlack,
    surface = DeepBlack,
    onPrimary = Color.Black,
    //Afecta al color de los titulos y artistas de las canciones de la pantalla principal
    onBackground = GasOrange,
    //Afecta a los titulos de las canciones de la seccion Mis listas, titulo de la pantalla y flecha de atras.
    //Ajustes y Nombre de la biblioteca, version que aparece en el menu, menu de las canciones cuando asignamos en listas
    onSurface = DeepSeaNeon,
    //Texto de buscar, menu, Ajustes -> texto, Pantalla listas, artista de la cancion
    onSurfaceVariant = SoftPinkNeon,
)