package com.ggs.parkuzpp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ParkUZPrimaryOrange,
    background = DarkAppBackground,
    surface = DarkSurfaceCard,
    onPrimary = Color.White, // White text on the orange primary button
    onBackground = DarkTextPrimary, // White primary text on deep navy background
    onSurface = DarkTextPrimary // White primary text on grey card surface
)

private val LightColorScheme = lightColorScheme(
    primary = ParkUZPrimaryOrange,
    background = LightAppBackground,
    surface = LightSurfaceCard,
    onPrimary = DarkTextPrimary, // Dark primary text on orange primary button (optional)
    onBackground = LightTextPrimary, // Dark primary text on light grey background
    onSurface = LightTextPrimary // Dark primary text on light card surface
)


@Composable
fun ParkUZTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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