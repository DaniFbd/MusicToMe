package com.myown.musictome.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.myown.musictome.R
import com.myown.musictome.data.local.PlaylistEntity

@Composable
fun AddToPlaylistDialog(
    playlists: List<PlaylistEntity>,
    onDismiss: () -> Unit,
    onSelect: (PlaylistEntity) -> Unit,
    idsWhereSongExists: List<Long>,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_list_dialog_title)) },
        text = {
            if (playlists.isEmpty()) {
                Text(stringResource(R.string.add_list_dialog_no_lists))
            } else {
                LazyColumn {
                    items(playlists) { playlist ->
                        val isAlreadyIn = idsWhereSongExists.contains(playlist.playlistId)

                        ListItem(
                            headlineContent = { Text(playlist.name) },
                            trailingContent = {
                                if (isAlreadyIn) {
                                    Icon(Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50))
                                }
                            },
                            modifier = Modifier.clickable {
                                if (!isAlreadyIn) onSelect(playlist)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}