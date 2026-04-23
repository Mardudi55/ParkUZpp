package com.ggs.parkuzpp.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // --- STANY ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRecaptchaVerified by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("EN") }

    // --- KOLORY POMOCNICZE DLA WIDŻETÓW ---
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Główny kontener z tłem z motywu aplikacji
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Sekcja Logo i Nazwa Aplikacji ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_withoutbg),
                contentDescription = "ParkUZ Logo",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ParkUZ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- 2. Główna Karta Logowania ---
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Nagłówek w karcie
                Text(
                    text = "Witaj ponownie",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zaloguj się, aby kontynuować nawigację.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- Pole Email ---
                Text(
                    text = "Email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            "twoj@email.pl",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- Pole Hasło ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasło",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Zapomniałeś hasła?",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { /* TODO: Nawigacja do resetu */ }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            "••••••••",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Lock Icon",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- WIDŻET RECAPTCHA ---
                RecaptchaWidget(
                    isVerified = isRecaptchaVerified,
                    onVerifyChange = { isRecaptchaVerified = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- Przycisk Logowania ---
                Button(
                    onClick = {
                        val emailTrimmed = email.trim()
                        val passwordTrimmed = password.trim()

                        if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
                            Toast.makeText(context, "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!isRecaptchaVerified) {
                            Toast.makeText(context, "Potwierdź, że nie jesteś robotem", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        val activity = context as? Activity
                        if (activity != null) {
                            authRepository.login(activity, emailTrimmed, passwordTrimmed) { success, error ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Zalogowano", Toast.LENGTH_SHORT).show()
                                    onNavigateToMap()
                                } else {
                                    Toast.makeText(context, "ERROR: ${error ?: "Nieznany błąd"}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Błąd kontekstu aplikacji", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Zaloguj się", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Tekst Rejestracji ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nie masz konta? ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Zarejestruj się",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. Przed-stopka: Ustawienia ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // JĘZYK
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedLanguage == "EN") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedLanguage = "EN" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EN",
                            color = if (selectedLanguage == "EN") MaterialTheme.colorScheme.primary else textSecondaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(borderColor))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (selectedLanguage == "PL") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedLanguage = "PL" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PL",
                            color = if (selectedLanguage == "PL") MaterialTheme.colorScheme.primary else textSecondaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DARK MODE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Brightness4,
                            contentDescription = null,
                            tint = textSecondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dark Mode",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = textSecondaryColor,
                            uncheckedTrackColor = borderColor
                        ),
                        modifier = Modifier.scale(0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Stopka z regulaminem ---
        Text(
            text = "Logując się, akceptujesz nasz Regulamin oraz Politykę Prywatności.",
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// --- Placeholder dla recaptchy (narazie atrapa) ---
@Composable
fun RecaptchaWidget(
    isVerified: Boolean,
    onVerifyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Zmieniono tło na ładnie współpracujące z motywem (bez sztywnych kolorów)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isVerified,
            onCheckedChange = { onVerifyChange(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )

        Text(
            text = "Nie jestem robotem",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "reCAPTCHA",
                tint = Color(0xFF4285F4),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "reCAPTCHA",
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}