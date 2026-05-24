package com.example.masakuy

import androidx.compose.runtime.Composable
import com.example.masakuy.presentation.navigation.AppNavHost
import com.example.masakuy.theme.MasakuyTheme

@Composable
fun App() {
    MasakuyTheme {
        AppNavHost()
    }
}