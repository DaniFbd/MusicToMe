package com.myown.musictome.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myown.musictome.R
import com.myown.musictome.data.local.PlaylistEntity
import com.myown.musictome.viewmodel.MusicViewModel
import kotlin.collections.emptyList

@Composable
fun PlaylistExpandableItem(
    playlist: PlaylistEntity,
    viewModel: MusicViewModel,
    onEditClick: (PlaylistEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val songsInPlaylist by viewModel.getSongsInPlaylist(playlist.playlistId)
        .collectAsState(initial = emptyList())
    var showMenu by remember { mutableStateOf(false) }

    Column {
        Box {
            ListItem(
                headlineContent = { Text(playlist.name, fontWeight = FontWeight.Bold) },
                supportingContent = { Text(stringResource(R.string.my_lists_number_songs, songsInPlaylist.size)) },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                trailingContent = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.combinedClickable(
                    onClick = { expanded = !expanded },
                    onLongClick = { showMenu = true }
                )
            )

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.btn_edit)) },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        onEditClick(playlist) // Llamamos a la edición
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.btn_delete), color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        showMenu = false
                        viewModel.deletePlaylist(playlist)
                    }
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 32.dp, end = 8.dp)) {
                songsInPlaylist.forEach { metadata ->
                    ListItem(
                        headlineContent = { Text(metadata.title, style = MaterialTheme.typography.bodyMedium) },
                        supportingContent = { Text(metadata.artist, style = MaterialTheme.typography.bodySmall) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removeSongFromPlaylist(playlist.playlistId,metadata.mediaId) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        },
                        modifier = Modifier.clickable {
                            viewModel.playSongFromPlaylist(songsInPlaylist,metadata.mediaId)
                        }
                    )
                }
            }
        }
    }
}