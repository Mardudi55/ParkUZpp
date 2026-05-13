package com.ggs.parkuzpp.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ggs.parkuzpp.bluetooth.BluetoothController
import com.ggs.parkuzpp.bluetooth.BluetoothRepository
import com.ggs.parkuzpp.bluetooth.SavedBluetoothDevice

@Composable
fun SettingsScreen() {

    val context = LocalContext.current

    val activity =
        context as Activity

    val repository =
        remember {
            BluetoothRepository(context)
        }

    val bluetoothController =
        remember {
            BluetoothController(activity)
        }

    val savedCars =
        remember {
            mutableStateListOf<SavedBluetoothDevice>()
        }

    // REFRESH FUNCTION

    fun refreshCars() {

        savedCars.clear()

        savedCars.addAll(
            repository.getSavedCars()
        )
    }

    // INITIAL LOAD

    LaunchedEffect(Unit) {

        refreshCars()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Zapisane Samochody",
            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ADD CAR BUTTON

        Button(
            onClick = {

                bluetoothController
                    .openCarPicker {

                        refreshCars()
                    }
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Dodaj Samochód")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // SAVED DEVICES LIST

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

                                refreshCars()
                            }
                        ) {

                            Text("Usuń")
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // DELETE ALL

        Button(
            onClick = {

                repository.clearCars()

                refreshCars()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Usuń wszystkie samochody")
        }
    }
}