package com.ggs.parkuzpp.main.camera


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    controller: CameraController
) {
    val capturedUri = viewModel.lastCapturedUri

    Box(modifier = Modifier.fillMaxSize())
    {
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
            Text("Zrób zdjęcie")
        }

        if (capturedUri != null) {
            AlertDialog(
                onDismissRequest = { viewModel.discardPhoto() },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.confirmPhoto()
                        viewModel.discardPhoto()
                    }) {
                        Text("Zapisz lokalizację")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.discardPhoto()
                    }) {
                        Text("Anuluj")
                    }
                },
                text = {
                    Image(
                        painter = rememberAsyncImagePainter(capturedUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}