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

private val DarkColorScheme = darkColorScheme(
    primary = ClinicalPrimary,
    secondary = ClinicalSecondary,
    tertiary = ClinicalTertiary,
    background = CalmBackgroundDark,
    surface = CalmSurfaceDark,
    onPrimary = CalmSurfaceLight,
    onSecondary = CalmSurfaceLight,
    onTertiary = CalmSurfaceLight,
    onBackground = CalmBackgroundLight,
    onSurface = CalmBackgroundLight
)

private val LightColorScheme = lightColorScheme(
    primary = ClinicalPrimary,
    secondary = ClinicalSecondary,
    tertiary = ClinicalTertiary,
    background = CalmBackgroundLight,
    surface = CalmSurfaceLight,
    onPrimary = CalmSurfaceLight,
    onSecondary = CalmSurfaceLight,
    onTertiary = CalmSurfaceLight,
    onBackground = CalmBackgroundDark,
    onSurface = CalmBackgroundDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors to keep clinical branding consistent
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
