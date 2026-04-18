package com.ggs.parkuzpp.main.camera

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

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
        lastCapturedUri = null
    }
}