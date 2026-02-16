package com.myown.musictome.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.myown.musictome.ui.screens.AllSongsScreen
import com.myown.musictome.ui.screens.GenresScreen
import com.myown.musictome.ui.screens.PlaylistsScreen
import com.myown.musictome.ui.theme.MusicToMeTheme
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.myown.musictome.ui.components.PlayerBar
import com.myown.musictome.ui.models.SortOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object AllSongs : BottomNavItem("Canciones", Icons.Filled.LibraryMusic)
    object Genres : BottomNavItem("Géneros", Icons.Filled.QueueMusic)
    object Playlists : BottomNavItem("Listas", Icons.Filled.PlaylistPlay)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val configuration = LocalConfiguration.current
    var isDarkTheme by remember { mutableStateOf(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) }

    //var isDarkTheme by rememberSaveable { mutableStateOf(isSystemInDarkTheme()) }
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.AllSongs) }
    var showMenu by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val musicFiles = remember { mutableStateListOf<DocumentFile>() }
    var sortOption by remember { mutableStateOf(SortOption.NAME) }

    //Para que la barra de notificaciones se vea como toca.
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, // o usa MaterialTheme.colorScheme.background
            darkIcons = !isDarkTheme // si es tema claro, iconos oscuros; si es oscuro, iconos claros
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                1
            )
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }


    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            uri?.let {
                saveMusicFolderUri(context, it)
                scope.launch {
                    val list = listAudioFiles(context, it)
                    musicFiles.clear()
                    musicFiles.addAll(list)
                }
            }
        }
    )
    // Cargar si ya se había seleccionado carpeta
    LaunchedEffect(Unit) {
        val savedUri = getSavedMusicFolderUri(context)
        savedUri?.let {
            val list = listAudioFiles(context, it)
            musicFiles.clear()
            musicFiles.addAll(list)
        }
    }

    MusicToMeTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MusicToMe") },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Seleccionar carpeta de música") },
                                onClick = {
                                    folderPickerLauncher.launch(null)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro")
                                },
                                onClick = {
                                    isDarkTheme = !isDarkTheme
                                    showMenu = false
                                }
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    PlayerBar()
                    NavigationBar {
                        listOf(
                            BottomNavItem.AllSongs,
                            BottomNavItem.Genres,
                            BottomNavItem.Playlists
                        ).forEach { item ->
                            NavigationBarItem(
                                selected = selectedItem == item,
                                onClick = { selectedItem = item },
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//            ) {
                when (selectedItem) {
                    is BottomNavItem.AllSongs -> AllSongsScreen(musicFiles,isDarkTheme,onSortOptionSelected = { sortOption = it },sortOption = sortOption, modifier = Modifier.padding(innerPadding))
                    is BottomNavItem.Genres -> GenresScreen()
                    is BottomNavItem.Playlists -> PlaylistsScreen()
                }
//            }
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Lanzador del selector de carpeta
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MusicToMeTheme {
                MainScreen()
            }
        }
    }
}
