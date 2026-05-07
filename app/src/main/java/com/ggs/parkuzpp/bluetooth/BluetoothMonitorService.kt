package com.ggs.parkuzpp.bluetooth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ggs.parkuzpp.R
import kotlinx.coroutines.*
import androidx.annotation.RequiresPermission
import androidx.core.content.edit
import com.ggs.parkuzpp.location.BluetoothTriggeredGPSService

class BluetoothMonitorService : Service() {
    private lateinit var receiver: BluetoothReceiver
    private var gpsService: BluetoothTriggeredGPSService? = null
    private var isBound = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var receiverRegistered = false


    private val locationConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            gpsService = (binder as BluetoothTriggeredGPSService.LocationBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            gpsService = null
        }
    }

    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())

        receiver = BluetoothReceiver { onCarDisconnected() }
        if (!receiverRegistered) {
            registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))
            receiverRegistered = true
        }
        bindService(
            Intent(this, BluetoothTriggeredGPSService::class.java),
            locationConnection,
            BIND_AUTO_CREATE
        )
        Log.d("BT", "Bluetooth monitor started")
    }

    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private fun onCarDisconnected() {
        Log.d("BT", "Getting location...")

        scope.launch {
            if (isBound && gpsService != null) {
                gpsService?.requestLocation(object : BluetoothTriggeredGPSService.LocationCallback {
                    override fun onLocationReceived(location: Location) {
                        saveLocation(location.latitude, location.longitude)
                        // TODO: Save location to the DB
                        Log.d("LOCATION", "Saved: ${location.latitude}, ${location.longitude}")
                    }

                    override fun onLocationFailed() {
                        Log.e("LOCATION", "Location request failed")
                    }
                })
            } else {
                Log.e("BT", "GPS service not bound")
            }
        }
    }

    private fun saveLocation(lat: Double, lon: Double) {
        val prefs = getSharedPreferences("locations", MODE_PRIVATE)
        prefs.edit {
            putString("last_lat", lat.toString())
                .putString("last_lon", lon.toString())
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "bluetooth_channel")
            .setContentTitle("Car monitor active")
            .setContentText("Watching for car disconnect")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "bluetooth_channel",
            "Bluetooth Monitor",
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    override fun onDestroy() {

        if (receiverRegistered) {
            unregisterReceiver(receiver)
            receiverRegistered = false
        }
        if (isBound) {
            unbindService(locationConnection)
            isBound = false
        }

        scope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}