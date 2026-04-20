package com.ggs.parkuzpp.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine

class UserTriggeredGPSService(private val context: Context) {

    private val fusedLocationClient by lazy {
        try {
            LocationServices.getFusedLocationProviderClient(context)
        } catch (e: Exception) {
            null
        }
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun isGmsAvailable(): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? {
        if (isGmsAvailable()) {

            return suspendCancellableCoroutine { continuation ->
                fusedLocationClient?.getCurrentLocation( // cannot be null if GMS is available
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null // cancellation signal
                )?.addOnSuccessListener { location ->
                    continuation.resume(location) { cause, _, _ -> onCancellation(cause) }
                }?.addOnFailureListener {
                    continuation.resume(null) { cause, _, _ -> onCancellation(cause) }
                }
            }
        } else {
            return suspendCancellableCoroutine { continuation ->
                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                        Log.e("LocationService", "Using GPS provider")
                        LocationManager.GPS_PROVIDER
                    }
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                        Log.e("LocationService", "Using Network provider")
                        LocationManager.NETWORK_PROVIDER
                    }
                    else -> {
                        Log.e("LocationService", "No provider available")
                        null
                    }
                }

                if (provider == null) {
                    continuation.resume(null) { cause, _, _ -> (::onCancellation)(cause) }
                    return@suspendCancellableCoroutine
                }

                Log.e("LocationService", "Requesting location from $provider")
                locationManager.getCurrentLocation(
                    provider,
                    null,
                    context.mainExecutor
                ) { location ->
                    if (location != null) {
                        Log.e("LocationService", "Got fresh location: $location")
                        continuation.resume(location) { cause, _, _ -> (::onCancellation)(cause) }
                    } else {
                        // cold fix, try last known
                        Log.e("LocationService", "Fresh location null, trying last known")
                        val lastKnown = locationManager.getLastKnownLocation(provider)
                        Log.e("LocationService", "Last known: $lastKnown")
                        continuation.resume(lastKnown) { cause, _, _ -> (::onCancellation)(cause) }
                    }
                }
            }
        }
    }

    private fun onCancellation(cause: Throwable) {
        // TODO: handle cancellation, e.g. log it
        Log.d("LocationService", "Location request cancelled: ${cause.message}")
    }

}
