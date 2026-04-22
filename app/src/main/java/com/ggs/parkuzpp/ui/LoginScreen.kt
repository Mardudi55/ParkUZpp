package com.ggs.parkuzpp.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository
import com.ggs.parkuzpp.auth.AuthValidator

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

    // 🔥 CAPTCHA STATE
    var selectedImages by remember { mutableStateOf(setOf<Int>()) }
    val correctImages = setOf(
        R.drawable.tree1,
        R.drawable.tree2,
        R.drawable.tree3
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 CAPTCHA
        Text("Wybierz wszystkie obrazy z drzewami")

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                val emailTrimmed = email.trim()
                val passwordTrimmed = password.trim()

                if (!AuthValidator.isLoginValid(emailTrimmed, passwordTrimmed)) {
                    Toast.makeText(context, "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 🔥 CAPTCHA CHECK
                if (selectedImages != correctImages) {
                    Toast.makeText(context, "Rozwiąż captchę poprawnie", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(
                                context,
                                error ?: "Błąd logowania",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    isLoading = false
                    Toast.makeText(context, "Błąd kontekstu", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zaloguj się")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Zarejestruj się",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(8.dp)
        )
    }
}

@Composable
fun CaptchaGrid(
    selected: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val images = listOf(
        R.drawable.cat,
        R.drawable.car,
        R.drawable.dawid,
        R.drawable.tree1,
        R.drawable.tree2,
        R.drawable.tree3
    )

    Column {
        for (row in 0 until 2) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    val imgRes = images[index]

                    Card(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clickable { onToggle(imgRes) }
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = imgRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )

                            // 🔥 PRZYCIEMNIENIE PO KLIKNIĘCIU
                            if (selected.contains(imgRes)) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}