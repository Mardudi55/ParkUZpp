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
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.ggs.parkuzpp.model.Coordinates
import com.ggs.parkuzpp.model.ParkSpot
import com.ggs.parkuzpp.model.ParkingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale

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

    fun setCaptured(uri: Uri) {
        lastCapturedUri = uri
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun confirmPhotoAndSave(context: Context) {
        val uri = lastCapturedUri ?: return

        viewModelScope.launch {
            isSaving = true
            try {
                val gpsService = UserTriggeredGPSService(context)
                val location = gpsService.getCurrentLocation()

                if (location == null) {
                    _errorEvent.emit("Nie udało się pobrać lokalizacji GPS.")
                    return@launch
                }

                val addressLabel = getAddressFromLocation(context, location.latitude, location.longitude)

                // --- KLUCZOWA POPRAWKA TUTAJ ---
                // Używamy uri.toString(), co daje poprawny format file:///data/...
                // Poprzednio używaliśmy uri.path, co dawało błędne /data/...
                val pathToString = uri.toString()

                val newSpot = ParkSpot(
                    active = true,
                    label = addressLabel,
                    photos = listOf(pathToString), // Zapisujemy poprawny String file://
                    coordinates = Coordinates(
                        lat = location.latitude,
                        lng = location.longitude
                    )
                )

                repository.deactivatePreviousSpots()
                val result = repository.saveParkingSpot(newSpot)

                if (result.isSuccess) {
                    // UWAGA: Nie czyścimy tu lastCapturedUri,
                    // robi to CameraScreen po odebraniu zdarzenia photoSaved
                    _photoSaved.emit(Unit)
                } else {
                    _errorEvent.emit(result.exceptionOrNull()?.message ?: "Błąd zapisu do bazy")
                }
            } catch (e: Exception) {
                _errorEvent.emit(e.message ?: "Wystąpił nieoczekiwany błąd")
            } finally {
                isSaving = false
            }
        }
    }

    // 1. Używamy po udanym ZAPISIE (czyści ekran, zostawia plik dla historii)
    fun clearUiState() {
        lastCapturedUri = null
    }

    // 2. Używamy przy ANULOWANIU (czyści ekran i kasuje fizyczny plik z dysku)
    fun discardPhoto() {
        lastCapturedUri?.let { uri ->
            // Pobieramy ścieżkę z Uri i kasujemy plik
            val path = uri.path
            if (path != null) {
                val file = java.io.File(path)
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
                "Nieznany adres"
            }
        } catch (e: Exception) {
            "Współrzędne: $lat, $lng"
        }
    }
}