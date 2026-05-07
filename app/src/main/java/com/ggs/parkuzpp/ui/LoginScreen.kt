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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository

/**
 * Composable function that displays the login screen.
 * Handles user authentication via email and password, recaptcha verification,
 * and provides navigation to the map or registration screens.
 *
 * @param isDarkTheme Indicates if the dark theme is currently active.
 * @param onThemeChange Callback triggered to toggle between dark and light themes.
 * @param onNavigateToMap Callback to navigate to the main map screen upon successful login.
 * @param onNavigateToRegister Callback to navigate to the registration screen.
 * @param currentLanguage The currently selected app language code (e.g., "en", "pl").
 * @param onLanguageChange Callback triggered to update the app's language preference.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToRegister: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRecaptchaVerified by remember { mutableStateOf(false) }

    val fillDataMsg = stringResource(R.string.toast_fill_data)
    val verifyRobotMsg = stringResource(R.string.toast_verify_robot)
    val loggedInMsg = stringResource(R.string.toast_logged_in)
    val contextErrorMsg = stringResource(R.string.error_context)

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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_withoutbg),
                contentDescription = stringResource(R.string.logo_desc),
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

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.login_welcome_back),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.login_subtitle),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.label_email),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(stringResource(R.string.placeholder_email), fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
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
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_password),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.forgot_password),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { /* Handle Password Reset */ }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(stringResource(R.string.placeholder_password), fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
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
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                RecaptchaWidget(
                    isVerified = isRecaptchaVerified,
                    onVerifyChange = { isRecaptchaVerified = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val emailTrimmed = email.trim()
                        val passwordTrimmed = password.trim()

                        if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
                            Toast.makeText(context, fillDataMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!isRecaptchaVerified) {
                            Toast.makeText(context, verifyRobotMsg, Toast.LENGTH_SHORT).show()
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
                                    Toast.makeText(context, loggedInMsg, Toast.LENGTH_SHORT).show()
                                    onNavigateToMap()
                                } else {
                                    Toast.makeText(context, "ERROR: $error", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(context, contextErrorMsg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        stringResource(R.string.btn_login),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_account),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.register_link),
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

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
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

                    Box(modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(borderColor))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
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
                            text = stringResource(R.string.dark_mode),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeChange(it) },
                        modifier = Modifier.scale(0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.footer_terms),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

/**
 * A mock reCAPTCHA widget allowing the user to verify they are not a robot.
 *
 * @param isVerified The current verification state.
 * @param onVerifyChange Callback triggered when the verification state changes.
 */
@Composable
fun RecaptchaWidget(isVerified: Boolean, onVerifyChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isVerified, onCheckedChange = { onVerifyChange(it) })
        Text(
            text = stringResource(R.string.recaptcha_label),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            tint = Color(0xFF4285F4),
            modifier = Modifier.size(24.dp)
        )
    }
}