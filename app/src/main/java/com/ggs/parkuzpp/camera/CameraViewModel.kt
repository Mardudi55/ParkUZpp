package com.ggs.parkuzpp.camera

import java.io.File
import android.net.Uri
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asSharedFlow
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * A [ViewModel] responsible for managing the state and business logic of the camera workflow.
 * It holds the temporarily captured image, allows the user to review it, and handles
 * the final decision (confirmation or deletion) before proceeding to the next screen.
 */
class CameraViewModel : ViewModel() {

    private val _photoSaved = MutableSharedFlow<Uri>()

    /**
     * A [SharedFlow] that emits the [Uri] of the confirmed photo.
     * UI components should collect this flow to navigate or perform side effects
     * once the user successfully accepts the captured image.
     */
    val photoSaved = _photoSaved.asSharedFlow()

    /**
     * Holds the [Uri] of the most recently captured photo.
     * Evaluates to null if no photo has been taken yet, or if the previous photo was discarded.
     */
    var lastCapturedUri by mutableStateOf<Uri?>(null)
        private set

    /**
     * Temporarily saves the captured image [Uri] into the ViewModel's state for review.
     *
     * @param uri The [Uri] of the recently taken photo.
     */
    fun setCaptured(uri: Uri) {
        lastCapturedUri = uri
    }

    /**
     * Confirms the currently held photo.
     * This triggers an emission to the [photoSaved] flow, notifying collectors
     * that the photo is ready to be used. Does nothing if no photo is currently held.
     */
    fun confirmPhoto() {
        val uri = lastCapturedUri ?: return
        viewModelScope.launch {
            _photoSaved.emit(uri)
        }
    }

    /**
     * Discards the currently held photo.
     * It safely attempts to delete the physical file from the device storage
     * to prevent memory leaks and orphaned files, and then clears the ViewModel's state.
     */
    fun discardPhoto() {
        lastCapturedUri?.path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }

        lastCapturedUri = null
    }
}