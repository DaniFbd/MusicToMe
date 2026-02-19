package com.myown.musictome.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "music_settings")

@Singleton
class MusicPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val LAST_SONG_ID = stringPreferencesKey("last_song_id")
    private val SHUFFLE_ON = booleanPreferencesKey("shuffle_on")
    private val REPEAT_ON = booleanPreferencesKey("repeat_on")

    suspend fun saveLastSong(songId: String) {
        context.dataStore.edit { it[LAST_SONG_ID] = songId }
    }

    val lastSongId: Flow<String?> = context.dataStore.data.map { it[LAST_SONG_ID] }

    suspend fun saveShuffle(enabled: Boolean) {
        context.dataStore.edit { it[SHUFFLE_ON] = enabled }
    }

    val isShuffleEnabled: Flow<Boolean> = context.dataStore.data.map { it[SHUFFLE_ON] ?: false }
}