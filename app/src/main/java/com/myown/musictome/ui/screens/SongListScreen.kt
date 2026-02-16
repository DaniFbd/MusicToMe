package com.myown.musictome.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.myown.musictome.model.Song
import com.myown.musictome.ui.components.SongItem

@Composable
fun SongListScreen() {
    val dummySongs = listOf(
        Song("1", "Starboy", "The Weeknd", "3:50"),
        Song("2", "Blinding Lights", "The Weeknd", "3:22"),
        Song("3", "Nightcall", "Kavinsky", "4:18")
    )

    LazyColumn {
        items(dummySongs) { song ->
            SongItem(song = song, onClick = { })
        }
    }
}