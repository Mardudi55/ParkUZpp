package com.ggs.parkuzpp.bluetooth

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission


object BluetoothUtils {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun chooseCarBluetooth(
        context: Context
    ) {
        Log.d("BT", "chooseCarBluetooth started")

        if (context !is Activity) {
            Log.e("BT", "Context is not Activity")
            return
        }

        val adapter = context.getSystemService(BluetoothManager::class.java).adapter

        if (adapter == null) {
            Toast.makeText(
                context,
                "Bluetooth not supported",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val repository = BluetoothRepository(context)

        val pairedDevices = adapter.bondedDevices.filter {
            !repository.isSavedCar(it.address)
        }.toList()

        if (pairedDevices.isEmpty()) {
            Toast.makeText(
                context,
                "No paired devices",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val deviceNames = pairedDevices.map {
            "${it.name}\n${it.address}"
        }.toTypedArray()

        context.runOnUiThread {
            AlertDialog.Builder(context)
                .setTitle("Choose your car Bluetooth")
                .setItems(deviceNames) { _, which ->
                    val selectedDevice = pairedDevices[which]

                    BluetoothRepository(context).saveCar(
                        selectedDevice.name ?: "Unknown Device",
                        selectedDevice.address
                    )

                    Toast.makeText(
                        context,
                        "Saved: ${selectedDevice.name}",
                        Toast.LENGTH_LONG
                    ).show()

                    startBluetoothMonitoring(context)
                }
                .show()
        }
    }

    fun startBluetoothMonitoring(
        context: Context
    ) {
        val intent = Intent(
            context,
            BluetoothMonitorService::class.java
        )
        context.startForegroundService(intent)
        Log.d("BT", "Bluetooth monitoring started")
    }
}