package com.myown.musictome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.myown.musictome.player.MusicPlayerHandler
import com.myown.musictome.ui.screens.SongListScreen
import com.myown.musictome.ui.theme.MusicToMeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var playerHandler: MusicPlayerHandler

    // Lanzador del selector de carpeta
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicToMeTheme {
                SongListScreen()
            }
        }
    }

    override fun onDestroy() {
        playerHandler.releaseController()
        super.onDestroy()
    }
}
