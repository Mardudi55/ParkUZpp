package com.ggs.parkuzpp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.scale
import com.ggs.parkuzpp.ui.theme.ParkUZPrimaryOrange
import com.ggs.parkuzpp.ui.theme.ParkUZStatusGreen

@Composable
fun AccountScreen(
    currentRoute: String? = null,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedLanguage by remember { mutableStateOf("EN") }

    // Dynamiczne kolory pobierane z motywu
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Tło menu prosto z motywu
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        // =========================================
        // PROFIL UŻYTKOWNIKA
        // =========================================
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Awatar z kropką statusu
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder zdjęcia zgodny z motywem
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = textSecondaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Alex Navigator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParkUZPrimaryOrange // Główny kolor zostaje
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // =========================================
        // ZAKŁADKI NAWIGACJI
        // =========================================
        DrawerMenuItem(
            text = "Map",
            icon = Icons.Default.Map,
            isSelected = currentRoute == "map",
            onClick = { onNavigate("map") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DrawerMenuItem(
            text = "History",
            icon = Icons.Default.History,
            isSelected = currentRoute == "history",
            onClick = { onNavigate("history") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DrawerMenuItem(
            text = "Account",
            icon = Icons.Default.AccountCircle,
            isSelected = currentRoute == "account",
            onClick = { /* TODO */ }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DrawerMenuItem(
            text = "Settings",
            icon = Icons.Default.Settings,
            isSelected = currentRoute == "settings",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = borderColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // =========================================
        // USTAWIENIA: JĘZYK
        // =========================================
        Text(
            text = "LANGUAGE",
            fontSize = 11.sp,
            color = textSecondaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
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
                    .background(if (selectedLanguage == "EN") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { selectedLanguage = "EN" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EN",
                    color = if (selectedLanguage == "EN") ParkUZPrimaryOrange else textSecondaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
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
                    color = if (selectedLanguage == "PL") ParkUZPrimaryOrange else textSecondaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // =========================================
        // USTAWIENIA: WYGLĄD
        // =========================================
        Text(
            text = "APPEARANCE",
            fontSize = 11.sp,
            color = textSecondaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)) // Tło kafelka z przełącznikiem
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Brightness4,
                    contentDescription = null,
                    tint = textSecondaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Dark Mode",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface, // Zależne od motywu
                    fontSize = 14.sp
                )
            }
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ParkUZPrimaryOrange,
                    uncheckedThumbColor = textSecondaryColor,
                    uncheckedTrackColor = borderColor
                ),
                modifier = Modifier.scale(0.8f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // =========================================
        // STOPKA
        // =========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ParkUZStatusGreen))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "V1.0.4",
                    fontSize = 11.sp,
                    color = textSecondaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Wyloguj",
                    tint = textSecondaryColor
                )
            }
        }
    }
}

// Komponent pomocniczy dla zakładki w menu
@Composable
fun DrawerMenuItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) ParkUZPrimaryOrange.copy(alpha = 0.08f) else Color.Transparent

    // Dynamiczny kolor tekstu ikonek: pomarańczowy jeśli zaznaczone, szarawy (z motywu) jeśli nie
    val contentColor = if (isSelected) ParkUZPrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lewy, pomarańczowy pasek zaznaczenia
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(if (isSelected) ParkUZPrimaryOrange else Color.Transparent)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}