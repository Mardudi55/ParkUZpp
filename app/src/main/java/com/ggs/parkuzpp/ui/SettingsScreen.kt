package com.ggs.parkuzpp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ggs.parkuzpp.bluetooth.BluetoothRepository
import com.ggs.parkuzpp.bluetooth.SavedBluetoothDevice

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val repository =
        remember {
            BluetoothRepository(context)
        }

    val savedCars =
        remember {
            mutableStateListOf<SavedBluetoothDevice>()
        }

    // LOAD SAVED DEVICES

    LaunchedEffect(Unit) {

        savedCars.clear()

        savedCars.addAll(
            repository.getSavedCars()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Saved Bluetooth Cars",
            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn {

            items(savedCars) { device ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = device.name
                            )

                            Text(
                                text = device.address,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }

                        Button(
                            onClick = {

                                repository.removeCar(
                                    device.address
                                )

                                savedCars.remove(device)
                            }
                        ) {

                            Text("Delete")
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                repository.clearCars()

                savedCars.clear()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Delete All Cars")
        }
    }
}