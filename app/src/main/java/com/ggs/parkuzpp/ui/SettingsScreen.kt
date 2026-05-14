package com.ggs.parkuzpp.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.bluetooth.BluetoothController
import com.ggs.parkuzpp.bluetooth.BluetoothRepository
import com.ggs.parkuzpp.bluetooth.SavedBluetoothDevice

/**
 * A screen that allows users to manage their saved Bluetooth car devices.
 * * This composable provides functionality to:
 * - View a list of currently paired and saved vehicles.
 * - Add new cars using a Bluetooth device picker.
 * - Remove specific cars or clear the entire list.
 */
@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val activity = context as Activity

    val repository = remember {
        BluetoothRepository(context)
    }

    val bluetoothController = remember {
        BluetoothController(activity)
    }

    val savedCars = remember {
        mutableStateListOf<SavedBluetoothDevice>()
    }

    /**
     * Synchronizes the [savedCars] state list with the data stored in the [BluetoothRepository].
     */
    fun refreshCars() {
        savedCars.clear()
        savedCars.addAll(repository.getSavedCars())
    }

    LaunchedEffect(Unit) {
        refreshCars()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.saved_car),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                bluetoothController.openCarPicker {
                    refreshCars()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_car))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = device.address,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                repository.removeCar(device.address)
                                refreshCars()
                            }
                        ) {
                            Text(stringResource(R.string.delete_car))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                repository.clearCars()
                refreshCars()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(stringResource(R.string.delete_all_cars))
        }
    }
}