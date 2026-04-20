package com.ggs.parkuzpp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToCamera: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- Miejsce na Mapę ---
         Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MAPA (placeholder)",
                fontSize = 20.sp
            )
        }

        // --- Dolne Menu ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onNavigateToHistory) {
                Text("Historia")
            }

            Button(onClick = onNavigateToAccount) {
                Text("Konto")
            }

            Button(onClick = onNavigateToCamera) {
                Text("Aparat")
            }
        }
    }
}