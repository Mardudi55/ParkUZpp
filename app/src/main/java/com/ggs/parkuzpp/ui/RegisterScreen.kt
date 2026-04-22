package com.ggs.parkuzpp.ui

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
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository
import com.ggs.parkuzpp.auth.AuthValidator

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // 🔥 POLA
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    // 🔥 CAPTCHA
    var selectedImages by remember { mutableStateOf(setOf<Int>()) }

    val correctImages = setOf(
        R.drawable.tree1,
        R.drawable.tree2,
        R.drawable.tree3
    )

    var isCaptchaChecked by remember { mutableStateOf(false) }
    var showCaptchaDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Rejestracja",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Powtórz hasło") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 CHECKBOX CAPTCHA
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showCaptchaDialog = true }
                .padding(8.dp)
        ) {
            Checkbox(
                checked = isCaptchaChecked,
                onCheckedChange = { showCaptchaDialog = true }
            )
            Text("Nie jestem robotem")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 BUTTON
        Button(
            onClick = {
                val emailTrimmed = email.trim()
                val passTrimmed = password.trim()
                val confirmPassTrimmed = confirmPassword.trim()

                if (emailTrimmed.isEmpty() || passTrimmed.isEmpty() || confirmPassTrimmed.isEmpty()) {
                    Toast.makeText(context, "Uzupełnij wszystkie dane", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!AuthValidator.isRegisterValid(emailTrimmed, passTrimmed, confirmPassTrimmed)) {
                    Toast.makeText(context, "Podane hasła nie są identyczne", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 🔥 CAPTCHA CHECK
                if (!isCaptchaChecked) {
                    Toast.makeText(context, "Potwierdź captchę", Toast.LENGTH_SHORT).show()
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

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Powrót")
        }
    }

    // 🔥 CAPTCHA DIALOG
    if (showCaptchaDialog) {
        AlertDialog(
            onDismissRequest = { showCaptchaDialog = false },
            confirmButton = {},
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Zaznacz wszystkie obrazy zawierające drzewa")

                    Spacer(modifier = Modifier.height(8.dp))

                    CaptchaGrid(
                        selected = selectedImages,
                        onToggle = { img ->
                            selectedImages = if (selectedImages.contains(img)) {
                                selectedImages - img
                            } else {
                                selectedImages + img
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (selectedImages == correctImages) {
                                isCaptchaChecked = true
                                showCaptchaDialog = false
                            } else {
                                Toast.makeText(context, "Źle rozwiązana captcha", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Sprawdź")
                    }
                }
            }
        )
    }
}