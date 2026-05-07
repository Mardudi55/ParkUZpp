package com.ggs.parkuzpp.bluetooth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import kotlinx.coroutines.*
import androidx.annotation.RequiresPermission

class BluetoothMonitorService : Service() {

    private lateinit var receiver: BluetoothReceiver

    private lateinit var gpsService:
            UserTriggeredGPSService

    private val scope =
        CoroutineScope(
            Dispatchers.IO + SupervisorJob()
        )
    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {

        super.onCreate()

        gpsService =
            UserTriggeredGPSService(this)

        createNotificationChannel()

        startForeground(
            1,
            createNotification()
        )

        receiver = BluetoothReceiver {

            onCarDisconnected()
        }

        registerReceiver(
            receiver,
            IntentFilter(
                BluetoothDevice.ACTION_ACL_DISCONNECTED
            )
        )

        Log.d(
            "BT",
            "Bluetooth monitor started"
        )
    }
    @RequiresPermission(
        anyOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private fun onCarDisconnected() {

        Log.d(
            "BT",
            "Getting location..."
        )

        scope.launch {

            try {

                val location =
                    gpsService.getCurrentLocation()

                Log.d(
                    "BT",
                    "Location result: $location"
                )

                if (location != null) {

                    saveLocation(
                        location.latitude,
                        location.longitude
                    )

                    Log.d(
                        "LOCATION",
                        "Saved: ${location.latitude}, ${location.longitude}"
                    )

                } else {

                    Log.d(
                        "LOCATION",
                        "Location NULL"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "LOCATION",
                    "ERROR: ${e.message}"
                )
            }
        }
    }

    private fun saveLocation(
        lat: Double,
        lon: Double
    ) {

        val prefs =
            getSharedPreferences(
                "locations",
                MODE_PRIVATE
            )

        prefs.edit()
            .putString(
                "last_lat",
                lat.toString()
            )
            .putString(
                "last_lon",
                lon.toString()
            )
            .apply()
    }

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            "bluetooth_channel"
        )
            .setContentTitle(
                "Car monitor active"
            )
            .setContentText(
                "Watching for car disconnect"
            )
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .build()
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    "bluetooth_channel",
                    "Bluetooth Monitor",
                    NotificationManager.IMPORTANCE_LOW
                )

            getSystemService(
                NotificationManager::class.java
            ).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        unregisterReceiver(receiver)

        scope.cancel()
    }

    override fun onBind(
        intent: android.content.Intent?
    ): IBinder? = null
}