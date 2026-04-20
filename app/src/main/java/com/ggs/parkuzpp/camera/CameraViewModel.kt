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

class CameraViewModel : ViewModel() {

    private val _photoSaved = MutableSharedFlow<Uri>()
    val photoSaved = _photoSaved.asSharedFlow()

    var lastCapturedUri by mutableStateOf<Uri?>(null)
        private set

    fun setCaptured(uri: Uri) {
        lastCapturedUri = uri
    }

    fun confirmPhoto() {
        val uri = lastCapturedUri ?: return
        viewModelScope.launch {
            _photoSaved.emit(uri)
        }
    }

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