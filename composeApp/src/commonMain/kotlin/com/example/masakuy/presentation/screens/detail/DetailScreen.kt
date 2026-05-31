package com.example.masakuy.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String,
    recipeName: String,
    budget: Int,
    viewModel: DetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId, recipeName, budget)
    }

    val recipe = uiState.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: recipeName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    recipe?.let {
                        IconButton(onClick = { viewModel.toggleFavorite(it.id, !it.isFavorite) }) {
                            Icon(
                                imageVector = if (it.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (it.isFavorite) OrangeMain else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = OrangeMain)
                        Spacer(Modifier.height(16.dp))
                        Text("Memuat detail resep...", color = Color.Gray)
                    }
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Gagal memuat detail: ${uiState.error}", color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                }
            }
            recipe != null -> {
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color(0xFF5A2400)), contentAlignment = Alignment.Center) {
                        Text("\uD83C\uDF7D\uFE0F", fontSize = 72.sp)
                    }
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(recipe.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(16.dp))
                        Row {
                            Surface(color = Color(0xFF5A2400), shape = RoundedCornerShape(12.dp)) {
                                Text("\uD83D\uDCB0 ${formatRp(recipe.estimatedCost)}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = OrangeMain, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Surface(color = Color(0xFF3A3A3A), shape = RoundedCornerShape(12.dp)) {
                                Text("\u23F1 ${recipe.estimatedTime} menit", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.width(10.dp))
                            Surface(color = Color(0xFF3A3A3A), shape = RoundedCornerShape(12.dp)) {
                                Text("\uD83D\uDCCA ${recipe.difficulty}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(Modifier.height(26.dp))
                        Text("Bahan-bahan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        if (recipe.ingredients.isEmpty()) {
                            Text("Tidak ada data bahan.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            recipe.ingredients.forEach { bahan ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(bahan.name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                    if (bahan.estimatedPrice > 0) Text(formatRp(bahan.estimatedPrice), color = OrangeMain, fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(26.dp))
                        Text("Cara Membuat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        if (recipe.instructions.isEmpty()) {
                            Text("Tidak ada instruksi.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            recipe.instructions.forEachIndexed { index, step ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                    Box(modifier = Modifier.size(28.dp).background(OrangeMain, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(step, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        Spacer(Modifier.height(26.dp))
                        Button(
                            onClick = { viewModel.toggleFavorite(recipe.id, !recipe.isFavorite) },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (recipe.isFavorite) Color(0xFFEF4444) else OrangeMain)
                        ) {
                            Text(
                                text = if (recipe.isFavorite) "\uD83D\uDDD1\uFE0F Hapus dari Favorit" else "\uD83E\uDD0D Simpan ke Favorit",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
