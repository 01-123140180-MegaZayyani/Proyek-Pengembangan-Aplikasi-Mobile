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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF888888),
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onSearchClick() }
                )
                Text(
                    "Masakuy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangeMain
                )
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF888888),
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        containerColor = Color(0xFFFFFBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Greeting
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    "Hai, mau makan",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "apa hari ini?",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("👋", fontSize = 26.sp)
                }
            }

            // Search bar dekoratif
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(14.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null,
                    tint = Color(0xFFBBBBBB), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Cari menu, bahan, atau budget...",
                    fontSize = 14.sp, color = Color(0xFFBBBBBB))
            }

            Spacer(Modifier.height(20.dp))

            // Budget section
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Budget kamu:", fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
                    Spacer(Modifier.height(10.dp))

                    // Chips budget
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        budgetOpts.forEach { (label, value) ->
                            val sel = selectedBudget == value
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (sel) OrangeMain else Color(0xFFF5F5F5))
                                    .border(
                                        1.dp,
                                        if (sel) OrangeMain else Color(0xFFE0E0E0),
                                        RoundedCornerShape(50.dp)
                                    )
                                    .clickable {
                                        selectedBudget = value
                                        customBudget = ""
                                        budgetError = ""
                                    }
                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    label,
                                    fontSize = 13.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) Color.White else Color(0xFF555555)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Input manual
                    OutlinedTextField(
                        value = customBudget,
                        onValueChange = {
                            customBudget = it.filter { c -> c.isDigit() }
                            if (customBudget.isNotEmpty()) selectedBudget = -1
                            budgetError = ""
                        },
                        placeholder = {
                            Text("Isi Sendiri",
                                fontSize = 13.sp, color = Color(0xFFBBBBBB))
                        },
                        prefix = { Text("Rp ", color = Color(0xFF555555), fontSize = 13.sp) },
                        isError = budgetError.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeMain,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedContainerColor = Color.White
                        )
                    )
                    if (budgetError.isNotEmpty()) {
                        Text(budgetError, color = Color(0xFFE53935), fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // CTA Button
            Button(
                onClick = {
                    val budget = if (customBudget.isNotEmpty())
                        customBudget.toIntOrNull() ?: 0
                    else selectedBudget
                    when {
                        budget <= 0 -> budgetError = "Pilih atau masukkan budget dulu"
                        budget < 5000 -> budgetError = "Minimal Rp 5.000"
                        else -> {
                            focusManager.clearFocus()
                            onRecommendationClick(budget)
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Cari Rekomendasi 🔍", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            // Rekomendasi Populer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rekomendasi Populer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A))
                TextButton(onClick = { onRecommendationClick(if (selectedBudget > 0) selectedBudget else if (customBudget.isNotEmpty()) customBudget.toIntOrNull() ?: 15000 else 15000) }) {
                    Text("Lihat semua >", fontSize = 12.sp, color = OrangeMain)
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                popularMenus.forEach { (name, price, emoji) ->
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .clickable { onRecommendationClick(15000) }, shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFFF3ED)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 34.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A),
                                maxLines = 2,
                                lineHeight = 16.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(Modifier.height(5.dp))
                            Text(price,
                                fontSize = 12.sp,
                                color = OrangeMain,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Accordion Menu Hemat Favorit
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandFavorit = !expandFavorit }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Menu Hemat Favorit", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    }
                    Text(if (expandFavorit) "▲" else "▼",
                        fontSize = 12.sp, color = Color(0xFF888888))
                }
                if (expandFavorit) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("Belum ada favorit. Simpan resep dari hasil rekomendasi!",
                            fontSize = 13.sp, color = Color(0xFF888888))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Accordion Menu yang Pernah Disimpan
            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandHistory = !expandHistory }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❤️", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("Menu yang Pernah Disimpan", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    }
                    Text(if (expandHistory) "▲" else "▼",
                        fontSize = 12.sp, color = Color(0xFF888888))
                }
                if (expandHistory) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (uiState.recipes.isEmpty()) {
                            Text("Belum ada resep tersimpan.",
                                fontSize = 13.sp, color = Color(0xFF888888))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.recipes.take(3).forEach { recipe ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onRecipeClick(recipe.id) },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🍽️", fontSize = 20.sp)
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(recipe.name, fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium)
                                            Text("Estimasi: Rp",
                                                fontSize = 11.sp, color = OrangeMain)
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


