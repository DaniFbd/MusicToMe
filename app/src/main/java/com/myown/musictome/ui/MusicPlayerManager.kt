package com.myown.musictome.ui

import android.media.MediaPlayer
import android.net.Uri
import android.content.Context

object MusicPlayerManager {

    //Crear un objeto MediaPlayer global o dentro de tu ViewModel para que persista la reproducción
    private var mediaPlayer: MediaPlayer? = null

    fun playSong(context: Context, songUri: Uri) {
        stopSong()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, songUri)
            setOnPreparedListener { start() }
            setOnErrorListener { _, _, _ -> false }
            prepareAsync()
        }
    }

    fun stopSong() {
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun getCurrentPosition(): Float {
        return mediaPlayer?.currentPosition?.toFloat() ?: 0f
    }

    fun getDuration(): Float {
        return mediaPlayer?.duration?.toFloat() ?: 0f
    }

}