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
import com.myown.musictome.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    viewModel: MusicViewModel = hiltViewModel(),
    onNavigateToLists: () -> Unit,
    modifier: Modifier = Modifier
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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: false
        val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false

        if (audioGranted) {
            viewModel.loadSongs()
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
                    Text(
                        stringResource(R.string.song_list_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick =  onNavigateToLists) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.my_lists_title)
                        )
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