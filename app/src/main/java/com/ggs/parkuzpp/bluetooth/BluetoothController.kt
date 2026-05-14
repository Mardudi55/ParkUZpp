package com.ggs.parkuzpp.bluetooth

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothController(
    private val activity: Activity
) {

    fun openCarPicker(
        onDeviceSaved: () -> Unit
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val bluetoothGranted =

                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED

            val locationGranted =

                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (
                bluetoothGranted &&
                locationGranted
            ) {

                BluetoothUtils
                    .chooseCarBluetooth(
                        activity,
                        onDeviceSaved
                    )

            } else {

                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1
                )
            }

        } else {

            BluetoothUtils
                .chooseCarBluetooth(
                    activity,
                    onDeviceSaved
                )
        }
    }
}