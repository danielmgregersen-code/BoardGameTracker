package com.daniel.boardgametracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFFFFB300),
    onPrimary        = Color(0xFF1A1400),
    primaryContainer = Color(0xFF3D2E00),
    onPrimaryContainer = Color(0xFFFFDEA0),
    secondary        = Color(0xFFCCA050),
    onSecondary      = Color(0xFF1A1200),
    secondaryContainer = Color(0xFF2A2010),
    onSecondaryContainer = Color(0xFFE8C88A),
    background       = Color(0xFF12121A),
    onBackground     = Color(0xFFE6E1D9),
    surface          = Color(0xFF1C1C28),
    onSurface        = Color(0xFFE6E1D9),
    surfaceVariant   = Color(0xFF2C2C3A),
    onSurfaceVariant = Color(0xFFBBB6B0),
    outline          = Color(0xFF555565),
    error            = Color(0xFFCF6679),
    onError          = Color(0xFF1A0010)
)

@Composable
fun BoardGameTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
