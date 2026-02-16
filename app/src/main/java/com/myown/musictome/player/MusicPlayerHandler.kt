package com.myown.musictome.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.myown.musictome.model.Song
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class MusicPlayerHandler@Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    var onMediaItemTransition: ((Int) -> Unit)? = null

    fun setupPlaylist(songs: List<Song>, startIndex: Int) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }

        val mediaItems = songs.map { song ->
            val uri = android.content.ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id.toLong()
            )
            MediaItem.fromUri(uri)
        }

        exoPlayer?.run {
            setMediaItems(mediaItems)
            seekTo(startIndex, 0L)
            prepare()
            play()
        }

        exoPlayer?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                exoPlayer?.currentMediaItemIndex?.let { index ->
                    onMediaItemTransition?.invoke(index)
                }
            }
        })
    }

    fun skipNext() { exoPlayer?.seekToNext() }
    fun skipPrevious() { exoPlayer?.seekToPrevious() }

    fun setRepeatMode(repeatAll: Boolean) {
        exoPlayer?.repeatMode = if (repeatAll) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    fun setShuffleMode(enabled: Boolean) {
        exoPlayer?.shuffleModeEnabled = enabled
    }

    fun togglePlayPause(play: Boolean) {
        if (play) {
            exoPlayer?.play()
        }else{
            exoPlayer?.pause()
        }
    }

    fun getCurrentPosition(): Long{
        return exoPlayer?.currentPosition ?: 0L
    }

    fun getDuration(): Long{
        return exoPlayer?.duration ?: 0L
    }
}