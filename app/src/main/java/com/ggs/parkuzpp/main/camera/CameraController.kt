package com.ggs.parkuzpp.main.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    val previewView: PreviewView
) {

    private var imageCapture: ImageCapture? = null

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.scaleType = PreviewView.ScaleType.FIT_CENTER
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(onResult: (Uri?) -> Unit) {
        val imageCapture = imageCapture ?: return

        val file = createImageFile(context)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onResult(Uri.fromFile(file))
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onResult(null)
                }
            }
        )
    }

    private fun createImageFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            .format(Date())

        val dir = File(context.filesDir, "images").apply { mkdirs() }

        return File(dir, "car_park_$timestamp.jpg")
    }
}