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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.ggs.parkuzpp.main.camera.CameraController
import com.ggs.parkuzpp.main.camera.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    controller: CameraController
) {
    // Zakładam, że lastCapturedUri to State<Uri?> (np. zadeklarowane jako mutableStateOf)
    val capturedUri = viewModel.lastCapturedUri

    Box(modifier = Modifier.fillMaxSize()) {
        // Podgląd z kamery
        AndroidView(
            factory = { controller.previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Przycisk robienia zdjęcia
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

        // Dialog widoczny tylko wtedy, gdy zrobiono zdjęcie
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
                        contentDescription = "Zrobione zdjęcie",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}