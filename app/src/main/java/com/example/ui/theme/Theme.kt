package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SolarGoldPrimary,
    onPrimary = SolarGoldOnPrimary,
    primaryContainer = SolarGoldContainer,
    onPrimaryContainer = SolarGoldOnContainer,
    secondary = EcoGreenSecondary,
    onSecondary = EcoGreenOnSecondary,
    secondaryContainer = EcoGreenContainer,
    onSecondaryContainer = EcoGreenOnContainer,
    tertiary = DeepSlateTertiary,
    onTertiary = SlateOnTertiary,
    tertiaryContainer = SlateContainer,
    onTertiaryContainer = SlateOnContainer,
    background = CampusBackground,
    onBackground = CampusOnSurface,
    surface = CampusSurface,
    onSurface = CampusOnSurface,
    surfaceVariant = CampusSurfaceDim,
    onSurfaceVariant = CampusOnSurfaceVariant,
    outline = CampusOutline,
    outlineVariant = CampusOutlineVariant,
    error = SignalRedError,
    onError = ErrorOn,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = SolarGoldPrimary,
    onPrimary = SolarGoldOnPrimary,
    primaryContainer = Color(0xFF6B4B00),
    onPrimaryContainer = SolarGoldContainer,
    secondary = EcoGreenSecondary,
    onSecondary = EcoGreenOnSecondary,
    secondaryContainer = Color(0xFF005236),
    onSecondaryContainer = EcoGreenContainer,
    tertiary = Color(0xFFCBD5E1),
    onTertiary = DeepSlateTertiary,
    tertiaryContainer = Color(0xFF1E293B),
    onTertiaryContainer = Color(0xFFF1F5F9),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    error = SignalRedError,
    onError = ErrorOn,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = ErrorContainer
)

@Composable
fun CampusVelocityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CampusTypography,
        content = content
    )
}
