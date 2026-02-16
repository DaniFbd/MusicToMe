package com.myown.musictome.ui

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.myown.musictome.ui.models.SongData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun convertToSongData(file: DocumentFile, context: Context): SongData? {
    val name = file.name ?: return null
    return try {
            file.name?.let { SongData(
                name = name,
                uri = file.uri,
                albumArt = getAlbumArtBytes(context, file.uri),
                duration = getAudioMetadata(file, context,MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L,
                artist = getAudioMetadata(file, context,MediaMetadataRetriever.METADATA_KEY_ARTIST),
                genre = getAudioMetadata(file, context,MediaMetadataRetriever.METADATA_KEY_GENRE)
            ) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getRetrieve(file: DocumentFile, context: Context): MediaMetadataRetriever{
    val uri = file.uri
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri)// Usamos el métodu que acepta la URI
    return retriever
}

private fun getAudioMetadata(file: DocumentFile, context: Context, metadataKey: Int): String? {
    val retriever = getRetrieve(file, context)
    var metadata: String? = null
    try {
        metadata = retriever.extractMetadata(metadataKey)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return metadata
}

private fun getAlbumArtBytes(context: Context, uri: Uri): ByteArray? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)
        retriever.embeddedPicture
    } catch (e: Exception) {
        null
    } finally {
        retriever.release()
    }
}