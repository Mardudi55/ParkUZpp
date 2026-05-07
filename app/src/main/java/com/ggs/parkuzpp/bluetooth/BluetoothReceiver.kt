package com.ggs.parkuzpp.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission

class BluetoothReceiver(
    private val onDisconnect: () -> Unit
) : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
            val device: BluetoothDevice? = intent.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )

            Log.d("BT", "Disconnected device: ${device?.name}")

            Log.d("BT", "Disconnected address: ${device?.address}")

            val repository = BluetoothRepository(context)

            val isKnownCar = repository.isSavedCar(device?.address)

            Log.d("BT", "Is known car: $isKnownCar")

            if (isKnownCar) {
                Log.d("BT", "KNOWN CAR DISCONNECTED")
                onDisconnect()

            } else {
                Log.d("BT", "Ignored device")
            }
        }
    }
}