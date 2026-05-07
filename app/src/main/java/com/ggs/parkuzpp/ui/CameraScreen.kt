package com.ggs.parkuzpp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.camera.CameraController
import com.ggs.parkuzpp.camera.CameraViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable screen that integrates the camera preview and allows the user to capture
 * a photo to save their parking location.
 *
 * @param viewModel The [CameraViewModel] managing state and business logic for the camera.
 * @param controller The [CameraController] handling the CameraX lifecycle and image capture.
 * @param onNavigateBack Callback to navigate back to the previous screen (e.g., the map) after saving.
 */
@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    controller: CameraController,
    onNavigateBack: () -> Unit
) {
    val capturedUri = viewModel.lastCapturedUri
    val isSaving = viewModel.isSaving
    val context = LocalContext.current

    val successMessage = stringResource(R.string.parked_successfully)

    LaunchedEffect(Unit) {
        viewModel.photoSaved.collectLatest {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearUiState()
            onNavigateBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collectLatest { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { controller.previewView },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                controller.takePhoto { uri ->
                    uri?.let { viewModel.setCaptured(it) }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text(stringResource(R.string.take_photo))
        }

        if (capturedUri != null) {
            AlertDialog(
                onDismissRequest = {
                    if (!isSaving) viewModel.discardPhoto()
                },
                confirmButton = {
                    TextButton(
                        onClick = @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                            viewModel.confirmPhotoAndSave(context)
                        },
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) stringResource(R.string.saving) else stringResource(R.string.save_location))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.discardPhoto() },
                        enabled = !isSaving
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                text = {
                    Image(
                        painter = rememberAsyncImagePainter(capturedUri),
                        contentDescription = stringResource(R.string.captured_photo_desc),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}