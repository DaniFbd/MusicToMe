package com.myown.musictome.player

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.myown.musictome.model.Song
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.core.net.toUri
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import javax.inject.Singleton

@Singleton
class MusicPlayerHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
) {
    private var mediaController: MediaController? = null
    var onMediaItemTransition: ((Int) -> Unit)? = null
    private var isInitializing = false

    fun setOnSongChangedListener(callback: (Int) -> Unit) {
        this.onMediaItemTransition = callback
    }

    fun setupPlaylist(songs: List<Song>, startIndex: Int, playWhenReady: Boolean = true) {
        if (mediaController != null || isInitializing) return
        isInitializing = true

        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                val controller = controllerFuture.get()
                mediaController = controller

                setMediaItemTransition(controller)

                // 2. Preparamos los MediaItems
                val mediaItems = createMediaItems(songs)

                // 3. ¡USAMOS EL CONTROLLER!
                initMediaController(controller, mediaItems, startIndex, playWhenReady)
            }catch (ex: Exception){
                Log.e("REPRODUCTOR", "Error al recuperar el controlador: ${ex.message}")
                isInitializing = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun initMediaController(controller: MediaController,
                                    items: List<MediaItem>,
                                    startIndex: Int,
                                    playWhenReady: Boolean){
        controller.let { controller ->
            controller.setMediaItems(items)
            controller.prepare()
            controller.seekTo(startIndex, 0L)
            if (playWhenReady) {
                controller.play()
            }
        }
    }

    private fun createMediaItems(songs: List<Song>) : List<MediaItem>{
        return songs.map { song ->
            val validatedTitle = song.title.ifBlank { "Título desconocido" }
            val validatedArtist = song.artist.ifBlank { "Artista desconocido" }

            val metadata = MediaMetadata.Builder()
                .setTitle(validatedTitle)
                .setArtist(validatedArtist)
                .setArtworkUri(getSongImgUri(song))
                .setDisplayTitle(validatedTitle)
                .setExtras(Bundle().apply { putString("media_id", song.id) })
                .build()

            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id.toLong()
            )

            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(uri)
                .setMediaMetadata(metadata)
                .build()
        }
    }

    private fun getSongImgUri(song: Song): Uri? {
        if (!song.imageUrl.isNullOrEmpty() && song.imageUrl.startsWith("content://")) {
            return song.imageUrl.toUri()
        }
        return null
    }

    private fun setMediaItemTransition(controller: MediaController) {
        if (mediaController?.currentMediaItem != null) {
            val index = mediaController!!.currentMediaItemIndex
            onMediaItemTransition?.invoke(index)
        }

        controller.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentIndex = controller.currentMediaItemIndex
                onMediaItemTransition?.invoke(currentIndex)
            }
        })
    }

    fun releaseController() {
        mediaController?.let {
            it.release()
            mediaController = null
            isInitializing = false
        }
    }

    fun skipNext() { mediaController?.seekToNext() }
    fun skipPrevious() { mediaController?.seekToPrevious() }

    fun setRepeatMode(repeatAll: Boolean) {
        mediaController?.repeatMode = if (repeatAll) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    fun setShuffleMode(enabled: Boolean) {
        mediaController?.shuffleModeEnabled = enabled
    }

    fun togglePlayPause(play: Boolean) {
        if (play) {
            mediaController?.play()
        }else{
            mediaController?.pause()
        }
    }

    fun getCurrentPosition(): Long{
        return mediaController?.currentPosition ?: 0L
    }

    fun getDuration(): Long{
        return mediaController?.duration ?: 0L
    }

    fun seekTo(pos: Long){
        mediaController?.seekTo(pos)
    }

    fun selectAndPlay(pos: Int){
        mediaController?.seekTo(pos,0L)
        mediaController?.prepare()
        mediaController?.play()
    }
}