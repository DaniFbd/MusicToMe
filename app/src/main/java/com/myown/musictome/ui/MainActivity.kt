package com.myown.musictome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.myown.musictome.ui.screens.MyListsScreen
import com.myown.musictome.ui.screens.SongListScreen
import com.myown.musictome.ui.theme.MusicToMeTheme
import com.myown.musictome.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    enum class Screen { SongList, MyLists }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSelection by viewModel.themeSelection.collectAsState(initial = "SYSTEM")
            MusicToMeTheme(themeSelection = themeSelection) {
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
