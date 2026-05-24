package com.example.masakuy.presentation.screens.recommendation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

private fun formatRupiah(amount: Int): String {
    return "Rp"
}

// Data dummy supaya screen bisa langsung dibuka
private val dummyRecipes = listOf(
    Recipe(
        id = "1",
        name = "Nasi Telur Kecap",
        image = "",
        estimatedCost = 11000,
        estimatedTime = 10,
        difficulty = "Mudah",
        isFavorite = false
    ),
    Recipe(
        id = "2",
        name = "Mi Sayur Telur",
        image = "",
        estimatedCost = 13500,
        estimatedTime = 15,
        difficulty = "Mudah",
        isFavorite = false
    ),
    Recipe(
        id = "3",
        name = "Tahu Cabe Garam",
        image = "",
        estimatedCost = 12000,
        estimatedTime = 15,
        difficulty = "Mudah",
        isFavorite = false
    ),
    Recipe(
        id = "4",
        name = "Tempe Goreng Bumbu",
        image = "",
        estimatedCost = 8000,
        estimatedTime = 10,
        difficulty = "Mudah",
        isFavorite = false
    ),
    Recipe(
        id = "5",
        name = "Sayur Bayam Bening",
        image = "",
        estimatedCost = 9000,
        estimatedTime = 20,
        difficulty = "Mudah",
        isFavorite = false
    ),
)

private val recipeEmojis = listOf("🍳", "🍜", "🥘", "🍱", "🥗", "🍲", "🫕", "🥙")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    budget: Int,
    viewModel: RecommendationViewModel = koinViewModel(),
    onRecipeClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getRecommendations(budget)
    }

    // Tampilkan dummy dulu kalau masih loading atau kosong
    val displayRecipes = if (uiState.recipes.isNotEmpty()) uiState.recipes else dummyRecipes
    val isShowingDummy = uiState.recipes.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Rekomendasi untuk",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            "Budget ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OrangeMain
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali",
                            tint = Color(0xFF444444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFFFBF7)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header info
                item {
                    if (uiState.isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF3ED))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = OrangeMain,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Meminta rekomendasi dari AI...",
                                fontSize = 13.sp, color = Color(0xFF888888))
                        }
                        Spacer(Modifier.height(4.dp))
                    } else if (isShowingDummy) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF3ED))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 16.sp)
                            Spacer(Modifier.width(10.dp))
                            Text("Ini contoh tampilan. Tekan 'Muat Rekomendasi AI' untuk hasil nyata.",
                                fontSize = 12.sp, color = Color(0xFF888888),
                                lineHeight = 18.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FFF4))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✨", fontSize = 16.sp)
                            Spacer(Modifier.width(10.dp))
                            Text(" resep ditemukan sesuai budget kamu!",
                                fontSize = 13.sp, color = Color(0xFF2E7D32))
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }

                // Error banner
                if (uiState.error != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("⚠️ ",
                                    fontSize = 13.sp, color = Color(0xFFE53935))
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.getRecommendations(budget) },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Coba Lagi", color = Color.White,
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Tombol load AI kalau masih dummy
                if (isShowingDummy && !uiState.isLoading) {
                    item {
                        Button(
                            onClick = { viewModel.getRecommendations(budget) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
                        ) {
                            Text("✨ Muat Rekomendasi AI", fontSize = 15.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // List resep
                itemsIndexed(displayRecipes) { index, recipe ->
                    RecipeItemCard(
                        recipe = recipe,
                        index = index,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }

                // Tips hemat
                item {
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("💡", fontSize = 20.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Tips Hemat", fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = Color(0xFF795548))
                                Spacer(Modifier.height(4.dp))
                                Text("Sesuaikan porsi dan bahan dengan stok yang kamu punya di rumah!",
                                    fontSize = 12.sp, color = Color(0xFF795548),
                                    lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeItemCard(
    recipe: Recipe,
    index: Int,
    onClick: () -> Unit
) {
    var isFav by remember { mutableStateOf(recipe.isFavorite) }
    val emoji = recipeEmojis[index % recipeEmojis.size]

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nomor urut
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(OrangeMain),
                contentAlignment = Alignment.Center
            ) {
                Text("", fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            // Emoji placeholder gambar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF3ED)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 30.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.name, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💰", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text("Estimasi: ",
                        fontSize = 12.sp, color = OrangeMain)
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏱️", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text("Waktu:  menit",
                        fontSize = 12.sp, color = Color(0xFF888888))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onClick() },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangeMain)
                ) {
                    Text("Lihat Detail", fontSize = 12.sp, color = OrangeMain)
                }
            }

            // Favorit icon
            IconButton(
                onClick = { isFav = !isFav },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    null,
                    tint = if (isFav) Color(0xFFE53935) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
