package com.myown.musictome.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.myown.musictome.model.Song
import androidx.core.net.toUri

class MusicLoader (private val context: Context) {
    fun fetchSongs(): List<Song> {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)


            while (it.moveToNext()) {
                val id = it.getLong(idColumn).toString()
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val durationMs = it.getLong(durationColumn)
                val albumId = it.getLong(albumIdColumn)

                val artUri = ContentUris.withAppendedId(
                    "content://media/external/audio/albumart".toUri(),
                    albumId
                ).toString()

                songList.add(Song(id, title, artist, durationMs, imageUrl = artUri))
            }
        }
        return songList
    }
}