package com.ggs.parkuzpp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.ui.theme.ParkUZPrimaryOrange
import com.ggs.parkuzpp.ui.theme.ParkUZStatusGreen

/**
 * Composable function that displays the side navigation menu (drawer).
 * Provides navigation options, language selection, theme toggling, and app version info.
 *
 * @param currentRoute The currently active navigation route to highlight the selected menu item.
 * @param isDarkTheme Indicates if the dark theme is currently active.
 * @param onThemeChange Callback triggered to toggle between dark and light themes.
 * @param onNavigate Callback triggered to navigate to a specific route.
 * @param onLogout Callback triggered when the user clicks the logout button.
 * @param currentLanguage The currently selected app language code (e.g., "en", "pl").
 * @param onLanguageChange Callback triggered to update the app's language preference.
 */
@Composable
fun MenuScreen(
    currentRoute: String? = null,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val textSecondaryColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        DrawerMenuItem(
            text = stringResource(R.string.menu_map),
            icon = Icons.Default.Map,
            isSelected = currentRoute == "map",
            onClick = { onNavigate("map") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DrawerMenuItem(
            text = stringResource(R.string.menu_history),
            icon = Icons.Default.History,
            isSelected = currentRoute == "history",
            onClick = { onNavigate("history") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DrawerMenuItem(
            text = stringResource(R.string.menu_account),
            icon = Icons.Default.AccountCircle,
            isSelected = currentRoute == "password",
            onClick = { onNavigate("password") }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = borderColor, thickness = 1.dp)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.heading_language),
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

            Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(borderColor))

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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.heading_appearance),
            fontSize = 11.sp,
            color = textSecondaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
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
                    text = stringResource(R.string.dark_mode),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
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

        Spacer(modifier = Modifier.height(32.dp))

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
                    contentDescription = stringResource(R.string.logout_desc),
                    tint = textSecondaryColor
                )
            }
        }
    }
}

/**
 * A stylized single item within the navigation drawer.
 *
 * @param text The display label for the menu item.
 * @param icon The vector icon representing the item.
 * @param isSelected Determines if the item is currently active, applying highlighting styles.
 * @param onClick Callback triggered when the item is pressed.
 */
@Composable
fun DrawerMenuItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) ParkUZPrimaryOrange.copy(alpha = 0.08f) else Color.Transparent
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
            contentDescription = null,
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