package com.myown.musictome.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.data.MusicLoader
import com.myown.musictome.data.MusicPreferences
import com.myown.musictome.data.local.PlaylistDao
import com.myown.musictome.data.local.PlaylistEntity
import com.myown.musictome.data.local.PlaylistSongCrossRef
import com.myown.musictome.data.local.SongMetadataEntity
import com.myown.musictome.data.local.toSong
import com.myown.musictome.di.PlayerModule
import com.myown.musictome.model.Song
import com.myown.musictome.player.MusicPlayerHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MusicViewModel @Inject constructor(
    @PlayerModule.AttributedContext private val context: Context,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher,
    private val playlistDao: PlaylistDao,
    private val playerHandler: MusicPlayerHandler,
    private val musicPrefs: MusicPreferences
) : ViewModel() {
    private var progressJob: Job? = null
    private var isPlayingFromCustomPlaylist = false
    private var lastClickTime = 0L
    private val _songs = mutableStateOf<List<Song>>(Collections.emptyList())

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _currentSong = mutableStateOf<Song?>(null)
    val currentSong: State<Song?> = _currentSong

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> = _isShuffleEnabled

    private val _isRepeatAllEnabled = mutableStateOf(false)
    val isRepeatAllEnabled: State<Boolean> = _isRepeatAllEnabled

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _totalDuration = mutableStateOf(0L)
    val totalDuration: State<Long> = _totalDuration

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredSongs = combine(snapshotFlow { _songs.value }, _searchQuery) { songs, query ->
        if (query.isBlank()) {
            songs
        } else {
            songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                        song.artist.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = Collections.emptyList()
    )

    val playlists = playlistDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            launch { playerHandler.isPlaying.collect { playing ->
                _isPlaying.value = playing
                if (playing) {
                    startProgressUpdate()
                } else {
                    progressJob?.cancel()
                }
            } }
            launch { playerHandler.shuffleEnabled.collect { _isShuffleEnabled.value = it } }
            launch { playerHandler.repeatEnabled.collect { _isRepeatAllEnabled.value = it } }
        }

        setupPlayerListeners()
        restoreLastSession()
    }

    private fun restoreLastSession(){
        viewModelScope.launch(ioDispatcher) {
            val songsFlow = snapshotFlow { _songs.value }

            combine(
                musicPrefs.lastSongId,
                musicPrefs.isShuffleEnabled,
                musicPrefs.isRepeatEnabled,
                songsFlow
            ) { id, shuffle, repeat, songs ->
                InitialState(id, shuffle, repeat, songs)
            }.collect { state ->
                if (state.songs.isEmpty()) return@collect

                if (_currentSong.value == null) {
                    val index = if (state.id != null) {
                        val foundIndex = state.songs.indexOfFirst { it.id == state.id }
                        if (foundIndex != -1) foundIndex else 0
                    } else {
                        //1º ejecucion de aplicacion
                        0
                    }
                    _currentSong.value = state.songs[index]
                    playerHandler.setupPlaylist(
                        state.songs,
                        startIndex = index,
                        playWhenReady = false,
                        isGeneralList = true,
                        initialShuffle = state.shuffle,
                        initialRepeat = state.repeat
                    )
                }
            }
        }
    }

    private fun setupPlayerListeners() {
        playerHandler.onMediaItemTransition = { mediaItem ->
            mediaItem?.let { item ->
                val song = Song(
                    id = item.mediaId,
                    title = item.mediaMetadata.title.toString(),
                    artist = item.mediaMetadata.artist.toString(),
                    duration = item.mediaMetadata.durationMs ?: 0L,
                    imageUrl = item.mediaMetadata.artworkUri.toString(),
                )
                _currentSong.value = song
                viewModelScope.launch {
                    musicPrefs.saveLastSong(song.id)
                }
            }
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            val metadata = SongMetadataEntity(
                mediaId = song.id,
                title = song.title,
                artist = song.artist,
                duration = song.duration,
                artUri = song.imageUrl
            )
            playlistDao.insertSongMetadata(metadata)
            playlistDao.addSongToPlaylist(
                PlaylistSongCrossRef(playlistId, song.id)
            )

            Toast.makeText(context, "Añadida a la lista", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            playlistDao.removeSongFromPlaylist(playlistId,songId)
        }
    }

    fun playSongFromPlaylist(songsParams: List<SongMetadataEntity>, songId: String) {
        viewModelScope.launch {
            isPlayingFromCustomPlaylist = true
            val songsList = songsParams.map { it.toSong() }
            val foundIndex = songsList.indexOfFirst { it.id == songId }
            if(foundIndex != -1){
                val shuffle = playerHandler.shuffleEnabled.value
                val repeat = playerHandler.repeatEnabled.value
                playerHandler.setupPlaylist(
                    songs = songsList,
                    startIndex = foundIndex,
                    playWhenReady = true,
                    initialShuffle = shuffle,
                    initialRepeat = repeat)
            }
        }
    }

    suspend fun getListsForSong(songId: String): List<Long> {
        return playlistDao.getPlaylistIdsForSong(songId)
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistDao.insertPlaylist(PlaylistEntity(name = name))
        }
    }

    fun updatePlaylist(playlist: PlaylistEntity, newName: String) {
        viewModelScope.launch(ioDispatcher) {
            playlistDao.insertPlaylist(playlist.copy(name = newName))
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistDao.deletePlaylist(playlist)
        }
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<SongMetadataEntity>> {
        return playlistDao.getSongsFromPlaylist(playlistId)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadSongs() {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            val loader = MusicLoader(context)
            val fetchedSongs = loader.fetchSongs()

            if (fetchedSongs.isNotEmpty()) {
                playerHandler.setGeneralPlaylist(fetchedSongs)
            }

            withContext(mainDispatcher) {
                _songs.value = fetchedSongs
                _isLoading.value = false
            }
        }
    }

    fun onSongClick(song: Song) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 500) return
        lastClickTime = currentTime

        val songsList = _songs.value
        val index = songsList.indexOfFirst { it.id == song.id }

        if (index != -1) {
            if (isPlayingFromCustomPlaylist || playerHandler.getCurrentMediaItemCount() == 0) {
                isPlayingFromCustomPlaylist = false
                playerHandler.setupPlaylist(
                    songs = songsList,
                    startIndex = index,
                    playWhenReady = true,
                    isGeneralList = true,
                    initialShuffle = _isShuffleEnabled.value,
                    initialRepeat = _isRepeatAllEnabled.value
                )
            }else{
                playerHandler.jumpToSong(index)
            }

        }
    }

    fun next() { playerHandler.skipNext() }
    fun previous() { playerHandler.skipPrevious() }

    fun toggleShuffle() {
        playerHandler.setShuffleMode(!_isShuffleEnabled.value)
    }

    fun toggleRepeat() {
        playerHandler.setRepeatMode(!_isRepeatAllEnabled.value)
    }

    fun togglePlayPause() {
        if (_currentSong.value != null) {
            val newState = !_isPlaying.value
            playerHandler.togglePlayPause(newState)
        }
    }

    fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch(ioDispatcher) {
            while (true) {
                withContext(mainDispatcher) {
                    val pos = playerHandler.getCurrentPosition()
                    val dur = playerHandler.getDuration()
                    _currentPosition.value = pos
                    _totalDuration.value = dur
                }
                delay(1000)
            }
        }
    }

    fun seekTo(position: Float) {
        val duration = totalDuration.value
        val newPosition = (position * duration).toLong()
        playerHandler.seekTo(newPosition)
    }

    private class InitialState(id1: String?, shuffle1: Boolean, repeat1: Boolean, songs1: List<Song>) {
        var songs: List<Song> = songs1
        var shuffle: Boolean = shuffle1
        var repeat: Boolean = repeat1
        var id: String? = id1
    }
}