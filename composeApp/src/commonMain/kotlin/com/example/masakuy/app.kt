package com.example.masakuy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.masakuy.presentation.navigation.AppNavHost
import com.example.masakuy.theme.MasakuyTheme

@Composable
fun App() {
    var isDarkMode by remember { mutableStateOf(true) }
    MasakuyTheme(darkTheme = isDarkMode) {
        AppNavHost(
            isDarkMode = isDarkMode,
            onDarkModeToggle = { isDarkMode = it }
        )
    }
}
