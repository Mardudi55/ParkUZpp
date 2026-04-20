package com.ggs.parkuzpp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.auth.AuthRepository

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // Stany pól tekstowych
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // Nowe pole powtórz hasło

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start // Wyrównanie do lewej, tak jak w Twoim XML
    ) {
        // --- Tytuł ---
        Text(
            text = "Rejestracja",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Pole Email ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Pole Hasło ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Pole Powtórz Hasło ---
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Powtórz hasło") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Przycisk Rejestracji ---
        Button(
            onClick = {
                val emailTrimmed = email.trim()
                val passTrimmed = password.trim()
                val confirmPassTrimmed = confirmPassword.trim()

                if (emailTrimmed.isEmpty() || passTrimmed.isEmpty() || confirmPassTrimmed.isEmpty()) {
                    Toast.makeText(context, "Uzupełnij wszystkie dane", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (passTrimmed != confirmPassTrimmed) {
                    Toast.makeText(context, "Podane hasła nie są identyczne", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                authRepository.register(emailTrimmed, passTrimmed) { success, error ->
                    isLoading = false

                    if (success) {
                        Toast.makeText(context, "Rejestracja OK", Toast.LENGTH_SHORT).show()
                        onNavigateToLogin()
                    } else {
                        Toast.makeText(context, error ?: "Błąd rejestracji", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zarejestruj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Przycisk Powrotu ---
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Powrót")
        }
    }
}