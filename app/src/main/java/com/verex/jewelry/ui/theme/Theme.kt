package com.verex.jewelry.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VerexGreen,
    secondary = VerexGreenDark,
    tertiary = VerexWhite,
    background = VerexGreen,
    surface = VerexGreenDark
)

private val LightColorScheme = lightColorScheme(
    primary = VerexGreenDark,
    secondary = VerexGreen,
    tertiary = VerexWhite,
    background = VerexGreen,
    surface = VerexWhite
)

@Composable
fun VerexJewelryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado para forzar siempre los colores institucionales
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}