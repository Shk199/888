package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MizanColorScheme = lightColorScheme(
    primary = Gold600,
    onPrimary = Color.White,
    primaryContainer = Gold100,
    onPrimaryContainer = Gold700,
    secondary = Royal800,
    onSecondary = Color.White,
    secondaryContainer = Royal100,
    onSecondaryContainer = Royal900,
    tertiary = Navy900,
    onTertiary = Color.White,
    background = Slate50,
    onBackground = Navy900,
    surface = Color.White,
    onSurface = Navy900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    error = Red600,
    onError = Color.White
)

@Composable
fun MizanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MizanColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MizanTheme(content = content)
}
