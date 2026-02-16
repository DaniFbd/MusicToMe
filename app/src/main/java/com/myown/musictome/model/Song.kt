package com.myown.musictome.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val imageUrl: String? = null
)
