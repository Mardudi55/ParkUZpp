package com.ggs.parkuzpp.camera

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.ggs.parkuzpp.model.Coordinates
import com.ggs.parkuzpp.model.ParkSpot
import com.ggs.parkuzpp.model.ParkingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

/**
 * ViewModel responsible for handling camera operations, confirming photos,
 * fetching user location, and saving the new parking spot to the repository.
 */
class CameraViewModel : ViewModel() {

    private val repository = ParkingRepository()

    private val _photoSaved = MutableSharedFlow<Unit>()
    val photoSaved = _photoSaved.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    var lastCapturedUri by mutableStateOf<Uri?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    /**
     * Sets the URI of the recently captured photo in the ViewModel state.
     *
     * @param uri The [Uri] of the captured image.
     */
    fun setCaptured(uri: Uri) {
        lastCapturedUri = uri
    }

    /**
     * Confirms the captured photo, fetches current GPS coordinates, resolves the address,
     * and saves the parking spot data to the database.
     *
     * @param context The Android context required for location services and string resources.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun confirmPhotoAndSave(context: Context) {
        val uri = lastCapturedUri ?: return

        viewModelScope.launch {
            isSaving = true
            try {
                val gpsService = UserTriggeredGPSService(context)
                val location = gpsService.getCurrentLocation()

                if (location == null) {
                    _errorEvent.emit(context.getString(R.string.error_gps))
                    return@launch
                }

                val addressLabel = getAddressFromLocation(context, location.latitude, location.longitude)
                val pathToString = uri.toString()

                val newSpot = ParkSpot(
                    active = true,
                    label = addressLabel,
                    photos = listOf(pathToString),
                    coordinates = Coordinates(
                        lat = location.latitude,
                        lng = location.longitude
                    )
                )

                repository.deactivatePreviousSpots()
                val result = repository.saveParkingSpot(newSpot)

                if (result.isSuccess) {
                    _photoSaved.emit(Unit)
                } else {
                    _errorEvent.emit(result.exceptionOrNull()?.message ?: context.getString(R.string.error_db_save))
                }
            } catch (e: Exception) {
                _errorEvent.emit(e.message ?: context.getString(R.string.error_unexpected))
            } finally {
                isSaving = false
            }
        }
    }

    /**
     * Clears the UI state by resetting the captured URI.
     * Designed to be called after a successful save operation to clear the screen while keeping the physical file.
     */
    fun clearUiState() {
        lastCapturedUri = null
    }

    /**
     * Discards the photo by clearing the UI state and deleting the physical file from the device storage.
     * Designed to be called when the user cancels the photo confirmation.
     */
    fun discardPhoto() {
        lastCapturedUri?.let { uri ->
            val path = uri.path
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
        lastCapturedUri = null
    }

    private fun getAddressFromLocation(context: Context, lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                listOfNotNull(address.thoroughfare, address.subThoroughfare, address.locality)
                    .joinToString(", ")
            } else {
                context.getString(R.string.address_unknown)
            }
        } catch (e: Exception) {
            context.getString(R.string.address_coordinates, lat, lng)
        }
    }
}