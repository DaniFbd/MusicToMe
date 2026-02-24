package com.myown.musictome.player

import android.content.ComponentName
import android.content.ContentResolver
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
import androidx.core.net.toUri
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.myown.musictome.data.MusicPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.myown.musictome.R
import com.myown.musictome.di.PlayerModule

@Singleton
class MusicPlayerHandler @Inject constructor(
    @PlayerModule.AttributedContext private val context: Context,
    private val exoPlayer: ExoPlayer,
    private val musicPrefs: MusicPreferences
) {
    private var cachedGeneralMediaItems: List<MediaItem> = emptyList()

    private val scope= CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled = _shuffleEnabled.asStateFlow()

    private val _repeatEnabled = MutableStateFlow(false)
    val repeatEnabled = _repeatEnabled.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private var mediaController: MediaController? = null
    var onMediaItemTransition: ((MediaItem?) -> Unit)? = null
    private var isInitializing = false

    fun setupPlaylist(
        songs: List<Song>,
        startIndex: Int,
        playWhenReady: Boolean = true,
        isGeneralList: Boolean = false,
        initialShuffle: Boolean,
        initialRepeat: Boolean
    ) {
        val items = if (isGeneralList && cachedGeneralMediaItems.isNotEmpty()) {
            cachedGeneralMediaItems
        } else {
            createMediaItems(songs)
        }

        //Si lo volvemos a llamar, reiniciamos lista de reproduccion
        mediaController?.let { controller ->
            initMediaController(controller, items, startIndex, playWhenReady, initialShuffle, initialRepeat)
            return
        }

        if (isInitializing) return
        isInitializing = true

        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                val controller = controllerFuture.get()
                mediaController = controller

                setMediaItemTransition(controller)

                initMediaController(controller, items, startIndex, playWhenReady, initialShuffle, initialRepeat)
            }catch (ex: Exception){
                Log.e("REPRODUCTOR", "Error al recuperar el controlador: ${ex.message}")
                isInitializing = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun initMediaController(controller: MediaController,
                                    items: List<MediaItem>,
                                    startIndex: Int,
                                    playWhenReady: Boolean,
                                    initialShuffle: Boolean,
                                    initialRepeat: Boolean){
        controller.let { controller ->
            controller.stop()
            controller.clearMediaItems()

            controller.shuffleModeEnabled = initialShuffle
            controller.repeatMode = if (initialRepeat) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF

            controller.setMediaItems(items)
            controller.seekTo(startIndex, 0L)
            controller.prepare()
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
                .setDurationMs(song.duration)
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
        val mediaStoreUri = song.imageUrl?.toUri()
        if (imageExist(mediaStoreUri)) {
            return mediaStoreUri
        }
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.resources.getResourcePackageName(R.drawable.default_album_art))
            .appendPath(context.resources.getResourceTypeName(R.drawable.default_album_art))
            .appendPath(context.resources.getResourceEntryName(R.drawable.default_album_art))
            .build()
    }

    private fun imageExist(uri: Uri?): Boolean {
        if (uri == null) return false
        return try{
            context.contentResolver.openInputStream(uri)?.use{
                it.close()
                true
            }?:false
        }catch (e: Exception){ false}
    }

    private fun setMediaItemTransition(controller: MediaController) {
        if (mediaController?.currentMediaItem != null) {
            onMediaItemTransition?.invoke(mediaController?.currentMediaItem)
        }

        controller.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                onMediaItemTransition?.invoke(mediaItem)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleEnabled.value = shuffleModeEnabled
                scope.launch { musicPrefs.saveShuffle(shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                val isRepeatAll = repeatMode == Player.REPEAT_MODE_ALL
                _repeatEnabled.value = isRepeatAll
                scope.launch { musicPrefs.saveRepeat(isRepeatAll) }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
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
    fun getCurrentMediaItemCount(): Int = mediaController?.mediaItemCount ?: 0

    fun jumpToSong(index: Int) {
        mediaController?.let { controller ->
            if (index in 0 until controller.mediaItemCount) {
                controller.seekTo(index, 0L)
                controller.play()
            }
        }
    }

    fun setGeneralPlaylist(songs: List<Song>) {
        if (cachedGeneralMediaItems.isEmpty()) {
            cachedGeneralMediaItems = createMediaItems(songs)
        }
    }
}