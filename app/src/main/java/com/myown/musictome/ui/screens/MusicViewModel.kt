package com.myown.musictome.ui.screens

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.data.MusicLoader
import com.myown.musictome.data.MusicPreferences
import com.myown.musictome.di.PlayerModule
import com.myown.musictome.model.Song
import com.myown.musictome.player.MusicPlayerHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.emptyList
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MusicViewModel @Inject constructor(
    @PlayerModule.AttributedContext private val context: Context,
    @Named("IODispatcher") private val ioDispatcher: CoroutineDispatcher,
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher,
    private val playerHandler: MusicPlayerHandler,
    private val musicPrefs: MusicPreferences
) : ViewModel() {
    private val _songs = mutableStateOf<List<Song>>(emptyList())
    val songs: State<List<Song>> = _songs

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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        playerHandler.setOnSongChangedListener { index ->
            _songs.value.getOrNull(index)?.let { song ->
                _currentSong.value = song
                viewModelScope.launch {
                    musicPrefs.saveLastSong(song.id)
                }
            }
        }
        updateProgress()
        viewModelScope.launch(ioDispatcher) {
            val songsFlow = snapshotFlow { _songs.value }

            combine(
                musicPrefs.lastSongId,
                musicPrefs.isShuffleEnabled,
                musicPrefs.isRepeatEnabled,
                songsFlow) { id,shuffle,repeat, songs ->
                InitialState(id,shuffle,repeat, songs)
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
                    playerHandler.setupPlaylist(state.songs, index, playWhenReady = false,state.shuffle, state.repeat)
                }
            }
        }
        viewModelScope.launch {
            playerHandler.shuffleEnabled.collect { valorReal ->
                _isShuffleEnabled.value = valorReal
            }
        }

        viewModelScope.launch {
            playerHandler.repeatEnabled.collect { valorReal ->
                _isRepeatAllEnabled.value = valorReal
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadSongs() {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            val loader = MusicLoader(context)
            val fetchedSongs = loader.fetchSongs()

            withContext(mainDispatcher) {
                _songs.value = fetchedSongs
                _isLoading.value = false
            }
        }
    }

    fun onSongClick(song: Song) {
        _currentSong.value = song
        _isPlaying.value = true
        viewModelScope.launch {
            musicPrefs.saveLastSong(song.id)
        }
        playerHandler.selectAndPlay(songs.value.indexOf(song))
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
            _isPlaying.value = newState
            playerHandler.togglePlayPause(newState)
        }
    }

    fun updateProgress() {
        viewModelScope.launch (ioDispatcher){
            while(true){
                if (isPlaying.value) {
                    withContext(mainDispatcher) {
                        val pos = playerHandler.getCurrentPosition()
                        val dur = playerHandler.getDuration()

                        _currentPosition.value = pos
                        _totalDuration.value = dur
                    }
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