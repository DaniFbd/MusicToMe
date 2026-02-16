package com.myown.musictome.ui.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myown.musictome.ui.MusicPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(private val musicPlayerManager: MusicPlayerManager) : ViewModel() {
    var currentSong by mutableStateOf<SongData?>(null)
    var isPlaying by mutableStateOf(false)
    var progress by mutableStateOf(0f)  // Progreso de la canción
    var duration by mutableStateOf(0f)  // Duración total de la canción

    fun togglePlayPause() {
        if (currentSong == null) return
        // pause/resume logic
        if (isPlaying) {
            musicPlayerManager.pause()
        } else {
            musicPlayerManager.resume()
        }
        isPlaying = !isPlaying
    }

    // Función para alternar la reproducción
    fun togglePlayback(context: Context, song: SongData) {
        if (isPlaying && currentSong?.uri == song.uri) {
            musicPlayerManager.stopSong()
            isPlaying = false
            currentSong = null
        } else {
            musicPlayerManager.playSong(context, song.uri)
            isPlaying = true
            currentSong = song // pon los datos reales aquí
            duration = song.duration.toFloat()  // Devuelve la duración total de la canción
            startProgressTracking()
        }
    }

    // Función para iniciar el seguimiento del progreso
    private fun startProgressTracking() {
        viewModelScope.launch {
            while (isPlaying) {
                progress = musicPlayerManager.getCurrentPosition()  // Posición actual de la canción
                delay(1000)  // Actualiza cada segundo
            }
        }
    }
}