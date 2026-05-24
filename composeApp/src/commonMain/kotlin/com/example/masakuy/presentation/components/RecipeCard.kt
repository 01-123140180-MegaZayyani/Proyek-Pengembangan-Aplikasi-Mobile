package com.example.masakuy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.theme.OrangeDark
import com.example.masakuy.theme.OrangeMain

@Composable
fun RecipeCard(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji di kiri
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = OrangeMain.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getRecipeEmoji(recipe.name),
                        fontSize = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info tengah
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = formatRupiah(recipe.estimatedCost),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${recipe.estimatedTime} mnt",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (recipe.difficulty.isNotEmpty()) {
                        Text(
                            text = recipe.difficulty,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Favorit di kanan
            if (recipe.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorit",
                    tint = OrangeMain,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun getRecipeEmoji(name: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("ayam") -> "🍗"
        lower.contains("soto") -> "🍲"
        lower.contains("nasi") -> "🍚"
        lower.contains("mie") || lower.contains("mi ") -> "🍜"
        lower.contains("sate") -> "🍢"
        lower.contains("ikan") -> "🐟"
        lower.contains("udang") -> "🦐"
        lower.contains("tahu") || lower.contains("tempe") -> "🫘"
        lower.contains("sayur") -> "🥬"
        lower.contains("sup") || lower.contains("sop") -> "🥣"
        lower.contains("rendang") || lower.contains("gulai") -> "🍖"
        lower.contains("bakso") -> "🍡"
        lower.contains("telur") -> "🥚"
        else -> "🍽️"
    }
}

private fun formatRupiah(amount: Int): String {
    return "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()
}