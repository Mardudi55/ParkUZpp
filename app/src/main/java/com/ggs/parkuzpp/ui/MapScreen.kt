package com.ggs.parkuzpp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import androidx.compose.ui.res.stringResource

@Composable
fun MapScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val noLocationText = stringResource(R.string.no_location_yet)
    val couldNotGetLocationText = stringResource(R.string.could_not_get_location)
    val saveSpotText = stringResource(R.string.save_parking_spot)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gps = remember { UserTriggeredGPSService(context) }
    var coordinates by remember { mutableStateOf(noLocationText) }

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
            Column {

                Text(
                    text = "MAPA (placeholder)",
                    fontSize = 20.sp
                )
                Text(text = coordinates)
                Button(onClick = {
                    scope.launch {
                        try {
                            val location = gps.getCurrentLocation()
                            coordinates = if (location != null) {
                                "Lat: ${location.latitude}, Lng: ${location.longitude}"
                            } else {
                                couldNotGetLocationText
                            }
                        } catch (_: SecurityException) {
                            coordinates = couldNotGetLocationText
                        }
                    }
                }) {
                    Text(saveSpotText)
                }
            }
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