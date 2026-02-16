package com.myown.musictome.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun saveMusicFolderUri(context: Context, uri: Uri) {
    val prefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("music_uri", uri.toString()).apply()
    context.contentResolver.takePersistableUriPermission(
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
}

fun getSavedMusicFolderUri(context: Context): Uri? {
    val prefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
    val uriString = prefs.getString("music_uri", null)
    return uriString?.let { Uri.parse(it) }
}

fun listAudioFiles(context: Context, folderUri: Uri): List<DocumentFile> {
    val docFile = DocumentFile.fromTreeUri(context, folderUri)
    return docFile?.listFiles()
        ?.filter { it.isFile && it.name?.endsWith(".mp3") == true }
        ?: emptyList()
}