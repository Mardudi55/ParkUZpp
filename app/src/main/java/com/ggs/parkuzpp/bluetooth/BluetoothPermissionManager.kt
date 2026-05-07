package com.ggs.parkuzpp.bluetooth

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object BluetoothPermissionManager {
    private fun checkPermissions(activity: Activity): Boolean {
        val bluetoothGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val locationGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val notifGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        return bluetoothGranted && locationGranted && notifGranted
    }

    fun requestPermissions(
        activity: Activity,
    ): Boolean {
        if (!checkPermissions(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                1
            )
            return checkPermissions(activity)
        }
        return true
    }
}