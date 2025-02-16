package com.example.addressopener.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Темна тема
private val DarkColorScheme = darkColorScheme(
    primary = DarkGreenPrimary,
    secondary = DarkBlueSecondary,
    tertiary = DarkYellowTertiary,
    background = Color(0xFFFFFFFF),  // Темний фон
    surface = Color(0xFFFFFFFF),    // Поверхня
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

// Світла тема
private val LightColorScheme = lightColorScheme(
    primary = LightGreenPrimary,
    secondary = LightBlueSecondary,
    tertiary = LightYellowTertiary,
    background = Color(0xFFFFFFFF),  // Світлий фон
    surface = Color(0xFFFFFFFF),    // Поверхня
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

@Composable
fun AddressOpenerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        typography = Typography, // Ваша кастомна типографія
        content = content
    )
}
