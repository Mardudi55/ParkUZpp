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
    // Przekazujemy nawigację jako lambdy (funkcje) - to najlepsza praktyka w Compose,
    // dzięki temu ekran jest niezależny od konkretnego systemu nawigacji.
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Pobieramy Context (odpowiednik requireContext() z Fragmentu)
    val context = LocalContext.current

    // Inicjalizujemy repozytorium tylko raz i zapamiętujemy je
    val authRepository = remember { AuthRepository() }

    // Stan pól tekstowych
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Stan przycisku i ładowania
    var isLoading by remember { mutableStateOf(false) }

    // Zwykła kolumna układa elementy pionowo (odpowiednik Twojego ConstraintLayout w tym przypadku)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Zewnętrzny margines 24dp jak w XML
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

        Spacer(modifier = Modifier.height(16.dp)) // Odstęp między polami

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

                isLoading = true // Wyłącza przycisk

                // AuthRepository wymaga Activity, więc bezpiecznie rzutujemy Context
                val activity = context as? Activity
                if (activity != null) {
                    authRepository.login(
                        activity,
                        emailTrimmed,
                        passwordTrimmed
                    ) { success, error ->
                        isLoading = false // Włącza przycisk z powrotem

                        if (success) {
                            Toast.makeText(context, "Login OK", Toast.LENGTH_SHORT).show()
                            onNavigateToMap() // Odpalenie nawigacji
                        } else {
                            Toast.makeText(
                                context,
                                "ERROR: ${error ?: "Nieznany błąd"}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    isLoading = false
                    Toast.makeText(context, "Błąd kontekstu aplikacji", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading, // Automatycznie reaguje na zmianę stanu isLoading
            modifier = Modifier.fillMaxWidth()
        ) { Text("Zaloguj się") }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Przycisk / Tekst Rejestracji ---
        Text(
            text = "Zarejestruj się",
            color = MaterialTheme.colorScheme.primary, // Używa koloru z Twojego Theme
            modifier = Modifier
                .clickable { onNavigateToRegister() } // Odpalenie nawigacji
                .padding(8.dp) // Zwiększa obszar kliknięcia
        )
    }
}