package com.myown.musictome.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // operaciones de listas

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    //operaciones de canciones y relaciones

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongMetadata(song: SongMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND mediaId = :mediaId")
    suspend fun removeSongFromPlaylist(playlistId: Long, mediaId: String)

    // Obtener todas las canciones de una lista específica
    @Query("""
        SELECT s.* FROM songs_metadata s
        INNER JOIN playlist_song_cross_ref ref ON s.mediaId = ref.mediaId
        WHERE ref.playlistId = :playlistId
    """)
    fun getSongsFromPlaylist(playlistId: Long): Flow<List<SongMetadataEntity>>

    // Comprobar si una canción ya está en una lista
    @Query("SELECT EXISTS(SELECT 1 FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND mediaId = :mediaId)")
    suspend fun isSongInPlaylist(playlistId: Long, mediaId: String): Boolean

    @Query("SELECT playlistId FROM playlist_song_cross_ref WHERE mediaId = :mediaId")
    suspend fun getPlaylistIdsForSong(mediaId: String): List<Long>
}