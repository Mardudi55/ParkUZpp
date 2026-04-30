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

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    controller: CameraController,
    onNavigateBack: () -> Unit // Funkcja do powrotu na poprzedni ekran (np. mapę)
) {
    val capturedUri = viewModel.lastCapturedUri
    val isSaving = viewModel.isSaving
    val context = LocalContext.current

    // Nasłuchiwanie na sygnał sukcesu (zapisano do bazy)
    LaunchedEffect(Unit) {
        viewModel.photoSaved.collectLatest {
            Toast.makeText(context, "Zaparkowano pomyślnie!", Toast.LENGTH_SHORT).show()
            viewModel.clearUiState() // ZOSTAWIA plik na dysku, żeby historia miała co wyświetlić
            onNavigateBack()
        }
    }

    // Nasłuchiwanie na błędy (np. brak GPS, odmowa dostępu w Firebase)
    LaunchedEffect(Unit) {
        viewModel.errorEvent.collectLatest { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Podgląd aparatu
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
            Text(stringResource(id = R.string.take_photo)) // Podmienione
        }

        // Okno dialogowe potwierdzające zapis lokalizacji i zdjęcia
        if (capturedUri != null) {
            AlertDialog(
                onDismissRequest = {
                    if (!isSaving) viewModel.discardPhoto() // USUWA plik przy kliknięciu w tło
                },
                confirmButton = {
                    TextButton(
                        onClick = @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                            viewModel.confirmPhotoAndSave(context)
                        },
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "Zapisywanie..." else "Zapisz lokalizację")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.discardPhoto() }, // USUWA plik przy "Anuluj"
                        enabled = !isSaving
                    ) {
                        Text("Anuluj")
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