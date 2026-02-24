package com.myown.musictome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.myown.musictome.ui.screens.MyListsScreen
import com.myown.musictome.ui.screens.SongListScreen
import com.myown.musictome.ui.theme.MusicToMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    enum class Screen { SongList, MyLists }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicToMeTheme {
                var currentScreen by remember { mutableStateOf(Screen.SongList) }

                when (currentScreen) {
                    Screen.SongList -> {
                        SongListScreen(
                            onNavigateToLists = { currentScreen = Screen.MyLists }
                        )
                    }
                    Screen.MyLists -> {
                        MyListsScreen(
                            onBack = { currentScreen = Screen.SongList }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
