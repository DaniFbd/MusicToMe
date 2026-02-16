package com.myown.musictome.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.myown.musictome.model.Song
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class MusicPlayerHandler@Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null

    fun playSong(song: Song) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }

        val uri = android.content.ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            song.id.toLong()
        )

        val mediaItem = MediaItem.fromUri(uri)

        exoPlayer?.run {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun togglePlayPause(play: Boolean) {
        if (play) {
            exoPlayer?.play()
        }else{
            exoPlayer?.pause()
        }
    }
}