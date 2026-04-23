package com.ggs.parkuzpp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import kotlinx.coroutines.launch

//DODAĆ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onOpenMenu: () -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val noLocationText = stringResource(R.string.no_location_yet)
    val couldNotGetLocationText = stringResource(R.string.could_not_get_location)
    val saveSpotText = stringResource(R.string.save_parking_spot)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gps = remember { UserTriggeredGPSService(context) }
    var coordinates by remember { mutableStateOf(noLocationText) }
    Box(

        modifier = Modifier.fillMaxSize()
    ) {
        // --- 1. TŁO MAPY (Placeholder) ---
        // Zostaje sztywne, bo to tylko zaślepka na prawdziwą mapę
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF3E3E3E)),
            contentAlignment = Alignment.Center
        ) {

            Column() {
                Text(
                    text = "MIEJSCE NA MAPĘ",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = coordinates)
            }
        }

        // --- 2. GÓRNY PASEK I WYSZUKIWARKA ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    // Zmiana Color.White na dynamiczny surface z motywu
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Szukaj",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant // Zmiana z Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Dokąd zmierzasz?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Zmiana z Color.Gray
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Mikrofon",
                        tint = MaterialTheme.colorScheme.primary // Zmiana z parkuzOrange
                    )
                }
            }
        }

        // --- 3. DOLNE ELEMENTY (Przycisk dodawania + Karta Parkingu) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                onClick = onNavigateToCamera,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface, // Zmiana z Color.White
                shadowElevation = 4.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
//                Row(
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "+",
//                        color = MaterialTheme.colorScheme.primary, // Zmiana z parkuzOrange
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Dodaj lokalizację",
//                        color = MaterialTheme.colorScheme.onSurface, // Zmiana z Color.Black
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 14.sp
//                    )
//                }

                Button(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    onClick = {
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
                    Text(
                        text = "+",
                        color = MaterialTheme.colorScheme.primary, // Zmiana z parkuzOrange
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dodaj lokalizację",
                        color = MaterialTheme.colorScheme.onSurface, // Zmiana z Color.Black
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Zmiana z Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Parking A1 - Centrum",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface // Zmiana z Color.Black
                        )
                        Text(
                            text = "85%",
                            color = Color(0xFF4CAF50), // Zostawiamy zielony na sztywno
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ul. Podgórna 50, Zielona Góra",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Zmiana z Color.Gray
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO: Akcja parkowania */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary // Zmiana z parkuzOrange
                        )
                    ) {
                        Text(
                            text = "P  Parkuj tutaj",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White // Zostawiamy biały na pomarańczowym guziku
                        )
                    }
                }
            }
        }
    }
}