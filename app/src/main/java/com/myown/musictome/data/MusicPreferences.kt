package com.myown.musictome.data

import android.content.Context
import android.util.Log
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
    private val THEME_KEY = stringPreferencesKey("theme_selection")
    private val MUSIC_FOLDER_URI = stringPreferencesKey("music_folder_uri")
    private val LIBRARY_TITLE = stringPreferencesKey("library_title")

    suspend fun saveLibraryTitle(title: String) {
        context.dataStore.edit { preferences ->
            preferences[LIBRARY_TITLE] = title
        }
    }

    val libraryTitle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LIBRARY_TITLE] ?: "MusicToMe"
    }

    suspend fun saveMusicFolder(uri: String) {
        context.dataStore.edit { preferences ->
            Log.d("DataStore", "Emitiendo: $uri")
            preferences[MUSIC_FOLDER_URI] = uri
        }
    }
    val musicFolderUri: Flow<String?> = context.dataStore.data.map { it[MUSIC_FOLDER_URI] }

    val themeSelection: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "SYSTEM"
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun saveLastSong(songId: String) {
        context.dataStore.edit { it[LAST_SONG_ID] = songId }
    }

    val lastSongId: Flow<String?> = context.dataStore.data.map { it[LAST_SONG_ID] }

    suspend fun saveShuffle(enabled: Boolean) {
        context.dataStore.edit { it[SHUFFLE_ON] = enabled }
    }

    val isShuffleEnabled: Flow<Boolean> = context.dataStore.data.map { it[SHUFFLE_ON] ?: false }

    suspend fun saveRepeat(enabled: Boolean) {
        context.dataStore.edit { it[REPEAT_ON] = enabled }
    }

    val isRepeatEnabled: Flow<Boolean> = context.dataStore.data.map { it[REPEAT_ON] ?: false }
}