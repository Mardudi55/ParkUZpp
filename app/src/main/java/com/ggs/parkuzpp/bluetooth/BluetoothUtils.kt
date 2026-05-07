package com.ggs.parkuzpp.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

object BluetoothUtils {

    @SuppressLint("MissingPermission")
    fun chooseCarBluetooth(
        context: Context
    ) {

        Log.d(
            "BT",
            "chooseCarBluetooth started"
        )

        if (context !is Activity) {

            Log.e(
                "BT",
                "Context is not Activity"
            )

            return
        }

        val adapter =
            BluetoothAdapter.getDefaultAdapter()

        if (adapter == null) {

            Toast.makeText(
                context,
                "Bluetooth not supported",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val repository =
            BluetoothRepository(context)

        val pairedDevices =

            adapter.bondedDevices

                .filter {

                    !repository.isSavedCar(
                        it.address
                    )
                }

                .toList()

        if (pairedDevices.isEmpty()) {

            Toast.makeText(
                context,
                "No paired devices",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val deviceNames =
            pairedDevices.map {

                "${it.name}\n${it.address}"

            }.toTypedArray()

        context.runOnUiThread {

            AlertDialog.Builder(context)

                .setTitle(
                    "Choose your car Bluetooth"
                )

                .setItems(deviceNames) { _, which ->

                    val selectedDevice =
                        pairedDevices[which]

                    BluetoothRepository(context)
                        .saveCar(

                            selectedDevice.name
                                ?: "Unknown Device",

                            selectedDevice.address
                        )

                    Toast.makeText(
                        context,
                        "Saved: ${selectedDevice.name}",
                        Toast.LENGTH_LONG
                    ).show()

                    startBluetoothMonitoring(
                        context
                    )
                }

                .show()
        }
    }

    fun startBluetoothMonitoring(
        context: Context
    ) {

        val intent =
            Intent(
                context,
                BluetoothMonitorService::class.java
            )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            context.startForegroundService(
                intent
            )

        } else {

            context.startService(
                intent
            )
        }

        Log.d(
            "BT",
            "Bluetooth monitoring started"
        )
    }
}