package com.example.masakuy.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineBanner(isOnline: Boolean) {
    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF323232))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.WifiOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Tidak ada koneksi internet. Beberapa fitur mungkin tidak tersedia.",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                lineHeight = 17.sp
            )
        }
    }
}
