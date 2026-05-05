package com.ggs.parkuzpp.location

import android.R.drawable.ic_menu_mylocation
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.ggs.parkuzpp.R
import com.google.android.gms.location.*

/**
 * A service responsible for retrieving user's current location once in the background.
 * It is used by Bluetooth Service in the background.
 * Location data is retrieved with a callback.
 */
class BluetoothTriggeredGPSService : Service() {

    /**
     * Interface used for setting up callbacks.
     */
    interface LocationCallback {
        /**
         * Method handling successful GPS access.
         * @param location The requested [Location] data.
         */
        fun onLocationReceived(location: Location)

        /**
         * Method handling failures of GPS access.
         */
        fun onLocationFailed()
    }

    /**
     * Class used for Android Service setup, used by internal Android Methods.
     */
    inner class LocationBinder : Binder() {
        fun getService() = this@BluetoothTriggeredGPSService
    }

    /**
     * Companion object with Notification Data.
     */
    companion object {
        const val CHANNEL_ID = "location_service_channel"
        const val NOTIFICATION_ID = 1001
    }

    private val binder = LocationBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fusedCallback: com.google.android.gms.location.LocationCallback
    private var locationCallback: LocationCallback? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onBind(intent: Intent?) = binder

    /**
     * Method that is used to request location from the service.
     * @param callback The [LocationCallback] callback that receives location.
     */
    fun requestLocation(callback: LocationCallback) {
        locationCallback = callback

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }

        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback.onLocationFailed()
            stopSelf()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback.onLocationReceived(GPSUtils.formatLocation(location)!!)
                stopSelf()
            } else {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L)
                    .setMaxUpdates(1)
                    .setWaitForAccurateLocation(true)
                    .build()

                fusedCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val loc = result.lastLocation
                        if (loc != null) callback.onLocationReceived(GPSUtils.formatLocation(loc)!!)
                        else callback.onLocationFailed()
                        fusedLocationClient.removeLocationUpdates(fusedCallback)
                        stopSelf()
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    request, fusedCallback, Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            callback.onLocationFailed()
            stopSelf()
        }
    }

    /**
     * Method used for building an OS notification informing user about the GPS Access.
     * It is required by the service for proper functioning.
     */
    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_bt_gps_access_title))
            .setContentText(getString(R.string.notification_bt_gps_access_desc))
            .setSmallIcon(ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
