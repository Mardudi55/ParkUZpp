package com.ggs.parkuzpp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.camera.CameraController
import com.ggs.parkuzpp.camera.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    controller: CameraController
) {
    val capturedUri = viewModel.lastCapturedUri

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
            Text(stringResource(id = R.string.take_photo)) // Podmienione
        }

        if (capturedUri != null) {
            AlertDialog(
                onDismissRequest = { viewModel.discardPhoto() },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.confirmPhoto()
                        viewModel.discardPhoto()
                    }) {
                        Text(stringResource(id = R.string.save_location)) // Podmienione
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.discardPhoto()
                    }) {
                        Text(stringResource(id = R.string.cancel)) // Podmienione
                    }
                },
                text = {
                    Image(
                        painter = rememberAsyncImagePainter(capturedUri),
                        contentDescription = stringResource(id = R.string.captured_photo_desc), // Podmienione
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}