package com.example.masakuy.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("👨\u200D🍳", fontSize = 56.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Masakuy",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OrangeMain
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Cari ide makan sesuai budgetmu.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Text("✉️", fontSize = 17.sp, modifier = Modifier.padding(start = 4.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor     = OrangeMain,
                    unfocusedBorderColor   = MaterialTheme.colorScheme.outline,
                    unfocusedContainerColor= MaterialTheme.colorScheme.surface,
                    focusedContainerColor  = MaterialTheme.colorScheme.surface,
                    focusedTextColor       = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor     = MaterialTheme.colorScheme.onSurface,
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Text("🔒", fontSize = 17.sp, modifier = Modifier.padding(start = 4.dp)) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor     = OrangeMain,
                    unfocusedBorderColor   = MaterialTheme.colorScheme.outline,
                    unfocusedContainerColor= MaterialTheme.colorScheme.surface,
                    focusedContainerColor  = MaterialTheme.colorScheme.surface,
                    focusedTextColor       = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor     = MaterialTheme.colorScheme.onSurface,
                )
            )

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeMain)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Masuk", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.login("guest", "guest") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, OrangeMain)
            ) {
                Text("Daftar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OrangeMain)
            }
        }
    }
}
