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

/**
 * A service responsible for retrieving the user's current location on demand.
 * It gracefully handles the presence or absence of Google Mobile Services (GMS),
 * falling back to the native Android framework LocationManager if GMS is unavailable.
 *
 * @property context The application or activity context used to access system services.
 */
class UserTriggeredGPSService(private val context: Context) {

    /**
     * A lazily initialized client for Google Play Services location API.
     * Evaluates to null if the necessary classes are not found on the device.
     */
    private val fusedLocationClient by lazy {
        try {
            LocationServices.getFusedLocationProviderClient(context)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * A lazily initialized native Android location manager.
     */
    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    /**
     * Retrieves the location using the native Android [LocationManager].
     * It attempts to use the GPS provider first, falling back to the Network provider.
     *
     * @return The user's [Location] or null if no provider is available or the request fails.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun getLocationViaAndroid(): Location? {
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
                    Log.e("LocationService", "Fresh location null, trying last known")
                    val lastKnown = locationManager.getLastKnownLocation(provider)
                    Log.e("LocationService", "Last known: $lastKnown")
                    continuation.resume(lastKnown) { cause, _, _ -> (::onCancellation)(cause) }
                }
            }
        }
    }

    /**
     * Retrieves the location using Google Mobile Services (GMS) via [FusedLocationProviderClient]
     * with a high accuracy priority.
     *
     * @return The user's [Location] or null if the request fails.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun getLocationViaGms(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient?.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )?.addOnSuccessListener { location ->
                continuation.resume(location) { cause, _, _ -> onCancellation(cause) }
            }?.addOnFailureListener {
                continuation.resume(null) { cause, _, _ -> onCancellation(cause) }
            }
        }
    }

    /**
     * Fetches the current location using the best available method (GMS or native)
     * and formats the coordinates to a standard precision.
     *
     * @return The formatted [Location] object, or null if the location could not be determined.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? {
        return GPSUtils.formatLocation(if (isGmsAvailable()) {
            getLocationViaGms()
        } else {
            getLocationViaAndroid()
        })
    }

    /**
     * Checks whether Google Play Services are available and functioning on the device.
     *
     * @return True if GMS is available, false otherwise.
     */
    private fun isGmsAvailable(): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }


    /**
     * Handles the cancellation of the location request coroutine.
     *
     * @param cause The [Throwable] that caused the coroutine to be canceled.
     */
    private fun onCancellation(cause: Throwable) {
        Log.d("LocationService", "Location request cancelled: ${cause.message}")
    }
}