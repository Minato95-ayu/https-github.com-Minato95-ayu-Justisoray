package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFF59E0B),     // Editorial Warm Amber Highlight
    secondary = Color(0xFF6366F1),   // Editorial Indigo Axis
    tertiary = Color(0xFF10B981),    // Editorial Emerald / Cyber Green
    background = Color(0xFF050505),  // Deepest Midnight Black
    surface = Color(0xFF0F0F0F),     // Editorial Dark Card Charcoal
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFCBD5E1)
  )

private val LightColorScheme = DarkColorScheme // Always use premium dark to maintain cinematic game atmosphere

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Lock theme setting to cinematic dark mode
  dynamicColor: Boolean = false, // Force custom premium dark theme to avoid generic dynamic schemes overriding the artistic theme
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
