package com.ggs.parkuzpp.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothController(
    private val activity: Activity
) {
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission") // BluetoothPermissionManager assures it's not missing
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state != BluetoothAdapter.STATE_ON) {
                    return
                }
                if (BluetoothPermissionManager.requestPermissions(activity)) {
                    BluetoothUtils.chooseCarBluetooth(activity)
                }
            }
        }
    }

    fun start() {
        activity.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    fun stop() {
        activity.unregisterReceiver(bluetoothStateReceiver)
    }
}