package com.myown.musictome.ui.screens

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.data.MusicLoader
import com.myown.musictome.model.Song
import com.myown.musictome.player.MusicPlayerHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
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
) : ViewModel() {
    private val _songs = mutableStateOf<List<Song>>(emptyList())
    val songs: State<List<Song>> = _songs

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _currentSong = mutableStateOf<Song?>(null)
    val currentSong: State<Song?> = _currentSong

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

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
        playerHandler.playSong(song)
    }

    fun togglePlayPause() {
        if (_currentSong.value != null) {
            val newState = !_isPlaying.value
            _isPlaying.value = newState
            playerHandler.togglePlayPause(newState)
        }
    }
}