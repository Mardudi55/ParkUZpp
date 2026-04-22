package com.ggs.parkuzpp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.ui.theme.ParkUZPrimaryOrange
import com.ggs.parkuzpp.ui.theme.lightGrayBg

// Model danych pod Firebase
data class ParkingHistoryItem(
    val id: String,
    val address: String,
    val date: String,
    val imageUrl: String // Na później, np. pod Coil
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onOpenMenu: () -> Unit
) {


    // ==========================================
    // TODO: [FIREBASE] MIEJSCE NA PODPIĘCIE BAZY
    // Tutaj zastąpisz 'mockData' zmienną ze stanem (np. z ViewModelu)
    // która nasłuchuje na zmiany w kolekcji Firebase Firestore.
    // Przykład: val historyItems by viewModel.historyItems.collectAsState()
    // ==========================================
    val mockData = listOf(
        ParkingHistoryItem("1", "ul. Marszałkowska 10", "24.05.2024, 14:30", ""),
        ParkingHistoryItem("2", "Plac Defilad 1", "22.05.2024, 09:15", "")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- 1. GÓRNY PASEK (TopBar) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onOpenMenu) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ParkUZPrimaryOrange)
            }

            Text(
                text = "ParkUZ",
                color = ParkUZPrimaryOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "ParkUZ Logo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                tint = Color.Unspecified
            )
        }

        // --- 2. GŁÓWNA ZAWARTOŚĆ ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Historia Parkingów",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101828), // Ciemny kolor tekstu z makiety
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Wyszukiwarka
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Szukaj lokalizacji...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj", tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGrayBg,
                    unfocusedContainerColor = lightGrayBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Lista zapisanych lokalizacji
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Zabezpieczenie przed ucięciem przez dolny pasek
            ) {
                items(mockData) { item ->
                    HistoryItemCard(item = item, parkuzOrange = ParkUZPrimaryOrange)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: ParkingHistoryItem, parkuzOrange: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder zdjęcia (szary prostokąt)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C3E50)), // Ciemny z makiet
                contentAlignment = Alignment.Center
            ) {
                // TODO: Jak Firebase będzie miało linki do zdjęć, użyjecie tu Coil (AsyncImage)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Teksty i przyciski
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.address,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Przycisk "Mapa"
                    OutlinedButton(
                        onClick = { /* TODO: Otwórz mapę ze współrzędnymi */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, parkuzOrange.copy(alpha = 0.3f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = parkuzOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mapa", color = parkuzOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Przycisk "Usuń"
                    OutlinedButton(
                        onClick = {
                            // ==========================================
                            // TODO: [FIREBASE] USUWANIE
                            // Tu wywołaj funkcję: viewModel.deleteHistoryRecord(item.id)
                            // ==========================================
                        },
                        modifier = Modifier
                            .width(48.dp)
                            .height(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}