package com.ggs.parkuzpp.camera

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

/**
 * A controller responsible for managing the CameraX lifecycle and use cases.
 * It handles initializing the camera preview, binding it to the UI, and capturing photos.
 *
 * @property context The application or activity context.
 * @property lifecycleOwner The lifecycle owner used to bind the camera's active state.
 * @property previewView The UI surface where the live camera feed is displayed.
 */
class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    val previewView: PreviewView
) {

    private var imageCapture: ImageCapture? = null

    /**
     * Initializes the camera provider and binds the preview and image capture use cases
     * to the provided [lifecycleOwner]. It defaults to the rear-facing camera and
     * scales the preview to fit the center of the [previewView].
     */
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
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

    /**
     * Captures a single photo and saves it to the internal application storage.
     *
     * @param onResult A callback invoked when the capture process completes.
     * It provides the [Uri] of the saved image on success, or null if an error occurred.
     */
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

    /**
     * Creates a newly timestamped file in the application's internal "images" directory.
     *
     * @param context The context used to access the internal files directory.
     * @return A newly created [File] instance ready for image data output.
     */
    private fun createImageFile(context: Context): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            .format(Date())

        val dir = File(context.filesDir, "images").apply { mkdirs() }

        return File(dir, "car_park_$timestamp.jpg")
    }
}