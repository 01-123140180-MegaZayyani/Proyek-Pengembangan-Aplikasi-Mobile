package com.example.masakuy.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Warna utama MasakuY ───────────────────────────────────────────────────────
private val MasakuyColorScheme = lightColorScheme(
    primary          = OrangeMain,
    onPrimary        = Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = TextLight,

    secondary        = OrangeLight,
    onSecondary      = Color.White,

    background       = Color(0xFFFFFBF7),   // putih hangat
    onBackground     = TextLight,

    surface          = Color.White,
    onSurface        = TextLight,

    surfaceVariant   = LightGray,
    onSurfaceVariant = DarkGray,

    error            = Color(0xFFE53935),
    onError          = Color.White,
)

@Composable
fun MasakuyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MasakuyColorScheme,
        typography  = MasakuyTypography,
        content     = content
    )
}