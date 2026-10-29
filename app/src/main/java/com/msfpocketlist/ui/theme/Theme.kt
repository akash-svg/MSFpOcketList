package com.msfpocketlist.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// MSF brand red with neutral surfaces, so contact data stays the focus.
private val LightColors = lightColorScheme(
    primary = Color(0xFFD5121B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410003),
    secondary = Color(0xFF5B5F6B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E2EE),
    onSecondaryContainer = Color(0xFF181C25),
    background = Color(0xFFF6F6F8),
    onBackground = Color(0xFF1A1B1F),
    surface = Color(0xFFF6F6F8),
    onSurface = Color(0xFF1A1B1F),
    surfaceVariant = Color(0xFFE3E3E8),
    onSurfaceVariant = Color(0xFF5E5F66),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF0F0F3),
    surfaceContainerHigh = Color(0xFFEAEAEE),
    surfaceContainerHighest = Color(0xFFE4E4E9),
    outline = Color(0xFF8E8F96),
    outlineVariant = Color(0xFFD9D9DF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB3AC),
    onPrimary = Color(0xFF680008),
    primaryContainer = Color(0xFF930010),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFC3C6D2),
    onSecondary = Color(0xFF2D303B),
    secondaryContainer = Color(0xFF434652),
    onSecondaryContainer = Color(0xFFE0E2EE),
    background = Color(0xFF111114),
    onBackground = Color(0xFFE4E4E8),
    surface = Color(0xFF111114),
    onSurface = Color(0xFFE4E4E8),
    surfaceVariant = Color(0xFF45464D),
    onSurfaceVariant = Color(0xFFC6C6CD),
    surfaceContainerLowest = Color(0xFF0C0C0E),
    surfaceContainerLow = Color(0xFF1B1B1F),
    surfaceContainer = Color(0xFF1F1F23),
    surfaceContainerHigh = Color(0xFF29292E),
    surfaceContainerHighest = Color(0xFF343439),
    outline = Color(0xFF909097),
    outlineVariant = Color(0xFF45464D),
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun PocketListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = AppShapes,
        content = content,
    )
}

/** Edge-to-edge + app theme; every activity's onCreate goes through here. */
fun ComponentActivity.setAppContent(content: @Composable () -> Unit) {
    enableEdgeToEdge()
    setContent { PocketListTheme(content) }
}
