package com.myown.musictome.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.myown.musictome.R

@Composable
fun ThemeSelectorDialog(
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_change_aspect)) },
        text = {
            Column {
                ThemeOption(stringResource(R.string.theme_system), "SYSTEM", onThemeSelected)
                ThemeOption(stringResource(R.string.theme_amoled), "AMOLED", onThemeSelected)
                ThemeOption(stringResource(R.string.theme_ocean), "OCEAN", onThemeSelected)
                ThemeOption(stringResource(R.string.theme_forest), "FOREST", onThemeSelected)
                ThemeOption(stringResource(R.string.theme_retro), "RETRO", onThemeSelected)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_close)) } }
    )
}