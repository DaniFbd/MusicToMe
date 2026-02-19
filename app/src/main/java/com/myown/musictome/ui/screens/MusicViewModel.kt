package com.myown.musictome.ui.screens

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.data.MusicLoader
import com.myown.musictome.data.MusicPreferences
import com.myown.musictome.model.Song
import com.myown.musictome.player.MusicPlayerHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MusicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    init {
        playerHandler.setOnSongChangedListener { index ->
            _songs.value.getOrNull(index)?.let { song ->
                _currentSong.value = song
                viewModelScope.launch(ioDispatcher) {
                    musicPrefs.saveLastSong(song.id)
                }
            }
        }
        updateProgress()
        viewModelScope.launch(ioDispatcher) {
            val songsFlow = snapshotFlow { _songs.value }

            combine(musicPrefs.lastSongId, songsFlow) { id, songs ->
                Pair(id, songs)
            }.collect { (id, songs) ->
                if (id != null && songs.isNotEmpty() && _currentSong.value == null) {
                    val index = songs.indexOfFirst { it.id == id }
                    if (index != -1) {
                        _currentSong.value = songs[index]
                        playerHandler.setupPlaylist(songs, index, playWhenReady = false)
                    }
                }
            }
        }
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
        viewModelScope.launch(ioDispatcher) {
            musicPrefs.saveLastSong(song.id)
        }
        playerHandler.selectAndPlay(songs.value.indexOf(song))
    }

    fun next() { playerHandler.skipNext() }
    fun previous() { playerHandler.skipPrevious() }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        playerHandler.setShuffleMode(_isShuffleEnabled.value)
    }

    fun toggleRepeat() {
        _isRepeatAllEnabled.value = !_isRepeatAllEnabled.value
        playerHandler.setRepeatMode(_isRepeatAllEnabled.value)
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
}