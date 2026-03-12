package com.myown.musictome.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myown.musictome.R
import com.myown.musictome.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsMenuContent(
    viewModel: MusicViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    val context = LocalContext.current
    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // disparador collect del init
            viewModel.updateMusicFolder(it.toString())
            scope.launch { drawerState.close() }
        }
    }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showEditTitleDialog by remember { mutableStateOf(false) }
    val customTitle by viewModel.libraryTitle.collectAsState()

    Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_title_change)) },
            supportingContent = { Text(customTitle) },
            leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
            modifier = Modifier.clickable { showEditTitleDialog = true }
        )

        // cambiar la carpeta de origen de la musica
        TextButton(onClick = { folderLauncher.launch(null) }) {
            Text(stringResource(R.string.settings_music_folder))
        }

        // Cambiar Temas
        TextButton(onClick = {showThemeDialog = true} ) {
            Text(stringResource(R.string.settings_change_aspect))
        }

        Spacer(modifier = Modifier.weight(1f))

        val context = LocalContext.current
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName

        Text(
            text = "v$versionName",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (showEditTitleDialog) {
        EditTitleDialog(
            currentTitle = customTitle,
            onDismiss = { showEditTitleDialog = false },
            onConfirm = { newTitle ->
                viewModel.updateLibraryTitle(newTitle)
                showEditTitleDialog = false
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { selectedTheme ->
                viewModel.saveTheme(selectedTheme)
                showThemeDialog = false
            }
        )
    }
}