package com.myown.musictome.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.myown.musictome.R
import com.myown.musictome.formatTime

@Composable
fun CurrentSongScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val song by viewModel.currentSong
    val isPlaying by viewModel.isPlaying
    val isShuffle by viewModel.isShuffleEnabled
    val isRepeat by viewModel.isRepeatAllEnabled
    val position by viewModel.currentPosition
    val duration by viewModel.totalDuration

    val sliderPosition = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        AsyncImage(
            model = song?.imageUrl ?: "https://via.placeholder.com/300",
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = painterResource(R.drawable.default_album_art),
            error = painterResource(R.drawable.default_album_art),
            fallback = painterResource(R.drawable.default_album_art),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = song?.title ?: "Desconocido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = song?.artist ?: "Artista", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(30.dp))

        Slider(
            value = sliderPosition,
            onValueChange = { viewModel.seekTo(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = formatTime(position), style = MaterialTheme.typography.bodySmall)
            Text(text = formatTime(duration), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(20.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            FloatingActionButton(
                onClick = { viewModel.toggleShuffle() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(if (isShuffle) Icons.Default.ShuffleOn else Icons.Default.Shuffle, null)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.previous() }) { Icon(Icons.Default.SkipPrevious, null, modifier = Modifier.size(48.dp)) }

                FloatingActionButton(
                    onClick = { viewModel.togglePlayPause() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                }

                IconButton(onClick = { viewModel.next() }) { Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(48.dp)) }
            }

            FloatingActionButton(
                onClick = { viewModel.toggleRepeat() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(if (isRepeat) Icons.Default.RepeatOn else Icons.Default.Repeat, null)
            }
        }
    }
}