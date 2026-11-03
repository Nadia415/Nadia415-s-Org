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

private val LightColorScheme = lightColorScheme(
  primary = NavyPrimary,
  onPrimary = Color.White,
  primaryContainer = NavyLight.copy(alpha = 0.25f),
  onPrimaryContainer = NavyDark,
  secondary = PurpleAccent,
  onSecondary = Color.White,
  secondaryContainer = PurpleAccentLight,
  onSecondaryContainer = PurpleAccentDark,
  tertiary = NavySecondary,
  onTertiary = Color.White,
  background = SlateBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondary,
  outline = BorderLight,
  error = DangerRed,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = NavyLight,
  onPrimary = NavyDark,
  primaryContainer = NavyPrimary,
  onPrimaryContainer = Color.White,
  secondary = PurpleAccent,
  onSecondary = Color.White,
  secondaryContainer = PurpleAccentDark,
  onSecondaryContainer = PurpleAccentLight,
  tertiary = NavySecondary,
  onTertiary = Color.White,
  background = Color(0xFF0F172A),
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = Color(0xFF475569),
  error = Color(0xFFF87171),
  onError = Color.Black
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent school branding
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
