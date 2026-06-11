package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val PremiumColorScheme =
  darkColorScheme(
    primary = PremiumPrimary,
    onPrimary = PremiumOnPrimary,
    primaryContainer = PremiumPrimaryContainer,
    onPrimaryContainer = PremiumOnPrimaryContainer,
    secondary = PremiumSecondary,
    secondaryContainer = PremiumSecondaryContainer,
    onSecondaryContainer = PremiumOnSecondaryContainer,
    background = PremiumBackground,
    surface = PremiumSurface,
    onBackground = PremiumOnBackground,
    onSurface = PremiumOnSurface,
    surfaceVariant = PremiumSurfaceVariant,
    onSurfaceVariant = PremiumOnSurfaceVariant,
    outline = PremiumOutline,
    outlineVariant = PremiumOutlineVariant,
    error = PremiumError,
    onError = PremiumOnError
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  // Always enforce our Premium Branded Royal Blue & Gold theme for all modes
  val colorScheme = PremiumColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
