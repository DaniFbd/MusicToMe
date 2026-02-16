package com.myown.musictome.ui.models

import android.net.Uri

data class SongData(
    val name: String,
    val artist: String?,
    val genre: String?,
    val duration: Long,
    val uri: Uri,
    val albumArt: ByteArray? = null
)