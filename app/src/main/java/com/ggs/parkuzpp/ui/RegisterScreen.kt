package com.ggs.parkuzpp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // --- STANY ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRecaptchaVerified by remember { mutableStateOf(false) }

    // Teksty Toastów
    val fillDataMsg = stringResource(R.string.toast_fill_data)
    val passMismatchMsg = stringResource(R.string.toast_passwords_not_match)
    val verifyRobotMsg = stringResource(R.string.toast_verify_robot)
    val regSuccessMsg = stringResource(R.string.toast_register_success)
    val regErrorMsg = stringResource(R.string.toast_register_error)

    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Logo ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_withoutbg),
                contentDescription = stringResource(R.string.logo_desc),
                modifier = Modifier.size(64.dp).padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ParkUZ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. Karta Rejestracji ---
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.register_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.register_subtitle),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email
                Text(text = stringResource(R.string.label_email), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text(stringResource(R.string.placeholder_email), fontSize = 13.sp) },
                    leadingIcon = {
                        Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hasło
                Text(text = stringResource(R.string.label_password), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(stringResource(R.string.placeholder_password), fontSize = 13.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Powtórz Hasło
                Text(text = stringResource(R.string.label_confirm_password), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text(stringResource(R.string.placeholder_password), fontSize = 13.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // reCAPTCHA (z LoginScreen)
                RecaptchaWidget(isVerified = isRecaptchaVerified, onVerifyChange = { isRecaptchaVerified = it })

                Spacer(modifier = Modifier.height(32.dp))

                // Przycisk
                Button(
                    onClick = {
                        val emailTrimmed = email.trim()
                        val passTrimmed = password.trim()
                        val confirmPassTrimmed = confirmPassword.trim()

                        if (emailTrimmed.isEmpty() || passTrimmed.isEmpty() || confirmPassTrimmed.isEmpty()) {
                            Toast.makeText(context, fillDataMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (passTrimmed != confirmPassTrimmed) {
                            Toast.makeText(context, passMismatchMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!isRecaptchaVerified) {
                            Toast.makeText(context, verifyRobotMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        authRepository.register(emailTrimmed, passTrimmed) { success, error ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, regSuccessMsg, Toast.LENGTH_SHORT).show()
                                onNavigateToLogin()
                            } else {
                                Toast.makeText(context, error ?: regErrorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.register_link), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link do logowania
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = stringResource(R.string.already_have_account), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Text(
                        text = stringResource(R.string.btn_login),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. Ustawienia ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Język (uproszczony widok jak w Login)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp) // Możesz zostawić 36.dp lub 40.dp dla lepszego klikania
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // PRZYCISK EN
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight() // Wypełnienie wysokości dla lepszego centrowania
                            // Tło nadajemy TYLKO gdy język jest wybrany
                            .background(if (currentLanguage == "en") MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                            .clickable { onLanguageChange("en") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EN",
                            color = if (currentLanguage == "en") MaterialTheme.colorScheme.primary else textSecondaryColor,
                            fontWeight = if (currentLanguage == "en") FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // LINIA ROZDZIELAJĄCA
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(borderColor))

                    // PRZYCISK PL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            // Tło nadajemy TYLKO gdy język jest wybrany
                            .background(if (currentLanguage == "pl") MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                            .clickable { onLanguageChange("pl") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PL",
                            color = if (currentLanguage == "pl") MaterialTheme.colorScheme.primary else textSecondaryColor,
                            fontWeight = if (currentLanguage == "pl") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Dark Mode
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness4, contentDescription = null, tint = textSecondaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.dark_mode), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                    }
                    Switch(checked = isDarkTheme, onCheckedChange = { onThemeChange(it) }, modifier = Modifier.scale(0.7f))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}