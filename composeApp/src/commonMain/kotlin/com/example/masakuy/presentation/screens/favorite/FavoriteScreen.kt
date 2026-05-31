package com.example.masakuy.presentation.screens.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

private fun formatRp(amount: Int): String {
    val s = amount.toString().reversed()
    val chunks = s.chunked(3).joinToString(".").reversed()
    return "Rp$chunks"
}

private val recipeEmojis = listOf("🍳","🍜","🌶️","🍱","🥗","🍲","🫕","🥙")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = koinViewModel(),
    onRecipeClick: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Favorit Saya", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("❤️", fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangeMain)
            }
        } else if (uiState.favorites.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🍽️", fontSize = 52.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text("Belum ada favorit", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Simpan resep favoritmu dari halaman rekomendasi!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.favorites, key = { it.id }) { recipe ->
                    val emoji = recipeEmojis[uiState.favorites.indexOf(recipe) % recipeEmojis.size]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 30.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(recipe.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.height(2.dp))
                                Text("${formatRp(recipe.estimatedCost)} · ${recipe.estimatedTime} menit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = { onRecipeClick(recipe.id, recipe.name) },
                                    modifier = Modifier.height(30.dp),
                                    shape = RoundedCornerShape(9.dp),
                                    contentPadding = PaddingValues(horizontal = 13.dp, vertical = 0.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeMain)
                                ) { Text("Lihat Resep", fontSize = 12.sp, color = OrangeMain, fontWeight = FontWeight.Bold) }
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = { viewModel.removeFavorite(recipe.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }
    }
}