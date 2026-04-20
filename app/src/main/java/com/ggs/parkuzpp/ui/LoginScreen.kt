package com.ggs.parkuzpp.ui


import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import com.ggs.parkuzpp.auth.AuthRepository

@Composable
fun LoginScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current

    val authRepository = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Spacer(modifier = Modifier.height(24.dp))

        // --- Przycisk Logowania ---
        Button(
            onClick = {
                val emailTrimmed = email.trim()
                val passwordTrimmed = password.trim()

                if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
                    Toast.makeText(context, "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                val activity = context as? Activity
                if (activity != null) {
                    authRepository.login(
                        activity,
                        emailTrimmed,
                        passwordTrimmed
                    ) { success, error ->
                        isLoading = false

                        if (success) {
                            Toast.makeText(context, "Zalogowano", Toast.LENGTH_SHORT).show()
                            onNavigateToMap()
                        } else {
//                          TODO: przyjazny toast dla użytkownika pod koniec developmentu
                            Toast.makeText(
                                context,
                                "ERROR: ${error ?: "Nieznany błąd"}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    isLoading = false
//                  TODO: przyjazny toast dla użytkownika pod koniec developmentu
                    Toast.makeText(context, "Błąd kontekstu aplikacji", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Zaloguj się") }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Przycisk / Tekst Rejestracji ---
        Text(
            text = "Zarejestruj się",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(8.dp)
        )
    }
}