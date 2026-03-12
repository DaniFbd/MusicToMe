package com.myown.musictome.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.myown.musictome.ui.components.SongItem
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.ui.components.BottomPlayerBar
import com.myown.musictome.ui.components.SearchComponent
import com.myown.musictome.R
import com.myown.musictome.model.Song
import com.myown.musictome.ui.components.AddToPlaylistDialog
import com.myown.musictome.ui.components.ThemeSelectorDialog
import com.myown.musictome.ui.theme.NeonGreen
import com.myown.musictome.ui.theme.neonGradient
import com.myown.musictome.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = hiltViewModel(),
    onNavigateToLists: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val songs by viewModel.filteredSongs.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading
    val currentSong by viewModel.currentSong
    val isPlaying by viewModel.isPlaying
    val position by viewModel.currentPosition
    val duration by viewModel.totalDuration
    val progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val playlists by viewModel.playlists.collectAsState()
    var songToAssign by remember { mutableStateOf<Song?>(null) }
    var idsWhereSongExists by remember { mutableStateOf<List<Long>>(emptyList()) }
    val selectedIds by viewModel.selectedSongIds.collectAsState()
    val isInSelectionMode by viewModel.isSelectionMode.collectAsState()
    var showMultiSelectDialog by remember { mutableStateOf(false) }
    val libraryTitle by viewModel.libraryTitle.collectAsState()
    val isNeon = MaterialTheme.colorScheme.primary == NeonGreen

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        //pedimos los permisos
        val permission = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: false
        permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false

        if (permission) {
            viewModel.retryLoad()
        }
    }

    songToAssign?.let { song ->
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { songToAssign = null },
            onSelect = { playlist ->
                viewModel.addSongToPlaylist(playlist.playlistId, song)
                songToAssign = null
            },
            idsWhereSongExists
        )
    }

    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    Scaffold (
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isInSelectionMode) {
                        Text("${selectedIds.size} seleccionadas")
                    } else {
                        Text(text= libraryTitle,
                            style = if (isNeon) {
                                MaterialTheme.typography.titleLarge.copy(
                                    brush = neonGradient()
                                )
                            } else {
                                MaterialTheme.typography.titleLarge
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (isInSelectionMode) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.btn_cancel))
                        }
                    }
                },
                actions = {
                    if (isInSelectionMode) {
                        IconButton(onClick = { showMultiSelectDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = stringResource(R.string.add_list_dialog_title))
                        }
                    } else {
                        IconButton(onClick =  onNavigateToLists) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = stringResource(R.string.my_lists_title)
                            )
                        }

                        IconButton(onClick = onOpenSettings ) {
                            Icon(imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            currentSong?.let { song ->
                BottomPlayerBar(
                    modifier =Modifier.navigationBarsPadding(),
                    song = song,
                    progress = progress,
                    isPlaying = isPlaying,
                    onNext = {viewModel.next()},
                    onPrevious = {viewModel.previous()},
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onClick = { showBottomSheet = true }
                )
            }
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            SearchComponent(
                query = query,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    // Este es el spinner de carga
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.song_list_loading),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else if (songs.isEmpty()) {
                    // Caso por si no encuentra nada o no hay permiso
                    Text(text = stringResource(R.string.song_list_not_found))
                } else {
                    // La lista real
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(songs) { song ->
                            SongItem(
                                song = song,
                                onClick = { viewModel.onSongClick(song) },
                                isSelectionMode = isInSelectionMode,
                                isSelected = selectedIds.contains(song.id),
                                onSelect = { viewModel.toggleSelection(song.id) },
                                onAddToPlaylist = {
                                    viewModel.viewModelScope.launch {
                                        idsWhereSongExists = viewModel.getListsForSong(song.id)
                                        songToAssign = song
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showMultiSelectDialog) {
            AddToPlaylistDialog(
                playlists = playlists,
                onDismiss = { showMultiSelectDialog = false },
                onSelect = { playlistId ->
                    viewModel.addSelectedToPlaylist(playlistId.playlistId)
                    showMultiSelectDialog = false
                },
                idsWhereSongExists
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                CurrentSongScreen(
                    viewModel = viewModel,
                    onBack = { showBottomSheet = false }
                )
            }
        }
    }
}