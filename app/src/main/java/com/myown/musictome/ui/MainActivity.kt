package com.myown.musictome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.myown.musictome.ui.components.SettingsMenuContent
import com.myown.musictome.ui.screens.MyListsScreen
import com.myown.musictome.ui.screens.SongListScreen
import com.myown.musictome.ui.theme.MusicToMeTheme
import com.myown.musictome.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    enum class Screen { SongList, MyLists }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSelection by viewModel.themeSelection.collectAsState(initial = "SYSTEM")
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            MusicToMeTheme(themeSelection = themeSelection) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            // Volvemos a poner la dirección normal para el contenido del menú
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                ModalDrawerSheet(
                                    modifier = Modifier.fillMaxHeight().width(300.dp)
                                ) {
                                    SettingsMenuContent(viewModel, scope, drawerState)
                                }
                            }
                        }
                    ) {
                        // Contenido principal de la App (también con dirección normal)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

                            var currentScreen by remember { mutableStateOf(Screen.SongList) }
                            when (currentScreen) {
                                Screen.SongList -> {
                                    SongListScreen(
                                        onNavigateToLists = { currentScreen = Screen.MyLists },
                                        onOpenSettings = { scope.launch { drawerState.open() } },
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
