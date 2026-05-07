package com.ggs.parkuzpp.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothController(
    private val activity: Activity
) {

    private val bluetoothStateReceiver =
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                if (
                    intent?.action ==
                    BluetoothAdapter.ACTION_STATE_CHANGED
                ) {

                    val state =
                        intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR
                        )

                    if (
                        state ==
                        BluetoothAdapter.STATE_ON
                    ) {

                        requestPermissions()
                    }
                }
            }
        }

    fun start() {

        activity.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED
            )
        )
    }

    fun stop() {

        activity.unregisterReceiver(
            bluetoothStateReceiver
        )
    }

    private fun requestPermissions() {

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
                    .chooseCarBluetooth(activity)

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
                .chooseCarBluetooth(activity)
        }
    }
}