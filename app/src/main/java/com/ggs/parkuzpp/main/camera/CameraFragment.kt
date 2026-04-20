package com.ggs.parkuzpp.main.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ggs.parkuzpp.ui.CameraScreen
import com.ggs.parkuzpp.ui.theme.ParkUZTheme

class CameraFragment : Fragment() {
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                controller.startCamera()
            } else {
                // TODO: Tutaj możesz dodać np. Toast z informacją o braku uprawnień
            }
        }

    private val viewModel: CameraViewModel by viewModels()

    private lateinit var previewView: PreviewView
    private lateinit var controller: CameraController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = PreviewView(requireContext())
        controller = CameraController(
            requireContext(),
            viewLifecycleOwner,
            previewView
        )

        (view as ComposeView).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ParkUZTheme {
                    CameraScreen(
                        viewModel = viewModel,
                        controller = controller
                    )
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            controller.startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}