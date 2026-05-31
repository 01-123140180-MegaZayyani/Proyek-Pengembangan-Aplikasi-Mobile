package com.example.masakuy.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

private val budgetOpts = listOf(
    "Rp10.000" to 10000,
    "Rp15.000" to 15000,
    "Rp20.000" to 20000,
    "Rp30.000" to 30000,
    "Rp50.000" to 50000,
)

private val popularMenus = listOf(
    Triple("Nasi Telur Kecap", "Rp11.000", "🍳"),
    Triple("Mi Goreng Sayur", "Rp13.500", "🍜"),
    Triple("Tumis Tahu Pedas", "Rp12.000", "🥘"),
)

private fun formatRp(amount: Int): String {
    val s = amount.toString().reversed()
    val chunks = s.chunked(3).joinToString(".").reversed()
    return "Rp$chunks"
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onRecommendationClick: (Int) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var selectedBudget by remember { mutableStateOf(-1) }
    var customBudget by remember { mutableStateOf("") }
    var budgetError by remember { mutableStateOf("") }
    var expandFavorit by remember { mutableStateOf(false) }
    var expandHistory by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("\uD83D\uDD0D", fontSize = 22.sp, modifier = Modifier.clickable { onSearchClick() })
                Text("Masakuy", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = OrangeMain)
                Text("\uD83D\uDD14", fontSize = 22.sp)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text("Hai, mau makan", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("apa hari ini?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(6.dp))
                    Text("👋", fontSize = 26.sp)
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("\uD83D\uDD0D", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Text("Cari menu, bahan, atau budget...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Budget kamu:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        budgetOpts.forEach { (label, value) ->
                            val sel = selectedBudget == value
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (sel) OrangeMain else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (sel) OrangeMain else MaterialTheme.colorScheme.outline, RoundedCornerShape(50.dp))
                                    .clickable { selectedBudget = value; customBudget = ""; budgetError = "" }
                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                            ) {
                                Text(label, fontSize = 13.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customBudget,
                        onValueChange = {
                            customBudget = it.filter { c -> c.isDigit() }
                            if (customBudget.isNotEmpty()) selectedBudget = -1
                            budgetError = ""
                        },
                        placeholder = { Text("Isi Sendiri", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        prefix = { Text("Rp ", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp) },
                        isError = budgetError.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeMain,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    if (budgetError.isNotEmpty()) {
                        Text(budgetError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val budget = if (customBudget.isNotEmpty()) customBudget.toIntOrNull() ?: 0 else selectedBudget
                    when {
                        budget <= 0 -> budgetError = "Pilih atau masukkan budget dulu"
                        budget < 5000 -> budgetError = "Minimal Rp 5.000"
                        else -> { focusManager.clearFocus(); onRecommendationClick(budget) }
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
            ) {
                Text("Cari Rekomendasi 🔍", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rekomendasi Populer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { onRecommendationClick(if (selectedBudget > 0) selectedBudget else customBudget.toIntOrNull() ?: 15000) }) {
                    Text("Lihat semua >", fontSize = 12.sp, color = OrangeMain)
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                popularMenus.forEach { (name, price, emoji) ->
                    Card(
                        modifier = Modifier.width(120.dp).clickable { onRecommendationClick(15000) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(68.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 34.sp) }
                            Spacer(Modifier.height(10.dp))
                            Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, lineHeight = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(Modifier.height(5.dp))
                            Text(price, fontSize = 12.sp, color = OrangeMain, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Accordion Menu Hemat Favorit
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expandFavorit = !expandFavorit }.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Menu Hemat Favorit", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(if (expandFavorit) "▲" else "▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (expandFavorit) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (uiState.favorites.isEmpty()) {
                            Text("Belum ada favorit. Simpan resep dari hasil rekomendasi!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.favorites.take(3).forEach { recipe ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { onRecipeClick(recipe.id) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🍽️", fontSize = 20.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(recipe.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            Text(formatRp(recipe.estimatedCost), fontSize = 11.sp, color = OrangeMain)
                                        }
                                    }
                                }
                                if (uiState.favorites.size > 3) {
                                    TextButton(onClick = onFavoriteClick) {
                                        Text("Lihat semua favorit >", fontSize = 12.sp, color = OrangeMain)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Accordion Menu yang Pernah Disimpan
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expandHistory = !expandHistory }.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❤️", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Menu yang Pernah Disimpan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text(if (expandHistory) "▲" else "▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (expandHistory) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (uiState.recipes.isEmpty()) {
                            Text("Belum ada resep tersimpan.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.recipes.take(3).forEach { recipe ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { onRecipeClick(recipe.id) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🍽️", fontSize = 20.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(recipe.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            Text(formatRp(recipe.estimatedCost), fontSize = 11.sp, color = OrangeMain)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}