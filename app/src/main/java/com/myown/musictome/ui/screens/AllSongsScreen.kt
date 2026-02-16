package com.myown.musictome.ui.screens

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.myown.musictome.ui.*
import com.myown.musictome.ui.components.SongList
import com.myown.musictome.ui.models.MusicPlayerViewModel
import com.myown.musictome.ui.models.SongData
import com.myown.musictome.ui.models.SortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllSongsScreen(
    musicFiles: List<DocumentFile>,
    isDarkTheme: Boolean,
    onSortOptionSelected: (SortOption) -> Unit,
    sortOption: SortOption,
    modifier: Modifier = Modifier) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var songs by remember(musicFiles, sortOption) {
        mutableStateOf<List<SongData>>(emptyList())
    }
    var isPlaying by remember { mutableStateOf(false) }
    var currentSongUri by remember { mutableStateOf<Uri?>(null) }
    val musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()

    LaunchedEffect(sortOption) {
        // Cargar canciones en un hilo de fondo
        launch {
            val processedSongs = withContext(Dispatchers.IO) {
                musicFiles.mapNotNull { convertToSongData(it, context) }
            }
            songs = processedSongs.sortedWith(
                when (sortOption) {
                    SortOption.NAME -> compareBy { it.name ?: "" }
                    SortOption.ARTIST -> compareBy { it.artist ?: "" }
                    SortOption.GENRE -> compareBy { it.genre ?: "" }
                    SortOption.DURATION -> compareByDescending { it.duration }
                    SortOption.MODIFIED_DATE -> compareByDescending {
                        File(it.uri.path ?: "").lastModified()
                    }
                }
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().border(1.dp, Color.Black),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "${musicFiles.size} canciones")
                    },
                    actions = {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface // Esto hará que el color del texto se adapte al tema
                            ),
                            onClick = { expanded = true },
                            contentPadding = PaddingValues(0.dp) // Elimina el padding interno para ajustarse a los elementos
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // Alineamos verticalmente el texto y el icono
                                horizontalArrangement = Arrangement.spacedBy(4.dp) // Espaciado entre el texto y el icono
                            ) {
                                Text(text = "Ordenar")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Ordenar")
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(text = { Text("Nombre") }, onClick = {
                                expanded = false
                                onSortOptionSelected(SortOption.NAME)
                            })
                            DropdownMenuItem(text = { Text("Artista") }, onClick = {
                                expanded = false
                                onSortOptionSelected(SortOption.ARTIST)
                            })
                            DropdownMenuItem(text = { Text("Género") }, onClick = {
                                expanded = false
                                onSortOptionSelected(SortOption.GENRE)
                            })
                            DropdownMenuItem(text = { Text("Duración") }, onClick = {
                                expanded = false
                                onSortOptionSelected(SortOption.DURATION)
                            })
                            DropdownMenuItem(text = { Text("Fecha de modificación") }, onClick = {
                                expanded = false
                                onSortOptionSelected(SortOption.MODIFIED_DATE)
                            })
                        }
                    },
                    windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
                )
            }
        ) { innerPadding ->
            SongList(
                songs = songs, // si lo tienes como extensión o como conversión },
                currentSong = musicPlayerViewModel.currentSong?.name, // o pásalo desde el estado de reproducción si lo tienes
                onSongClick = { song ->
                    // Qué hacer cuando se pulsa una canción
                    musicPlayerViewModel.togglePlayback(context, song)
                },
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            )
        }
    }
}