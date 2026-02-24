package com.myown.musictome.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myown.musictome.model.Song

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val name: String
)

@Entity(tableName = "songs_metadata")
data class SongMetadataEntity(
    @PrimaryKey
    val mediaId: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val artUri: String?
)

@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlistId", "mediaId"]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val mediaId: String
)

fun SongMetadataEntity.toSong(): Song {
    return Song(
        id = this.mediaId,
        title = this.title,
        artist = this.artist,
        duration = this.duration,
        imageUrl = this.artUri
    )
}