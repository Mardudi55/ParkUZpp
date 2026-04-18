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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class CameraFragment : Fragment() {
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                controller.startCamera()
            } else {
                // możesz tu dać Toast albo wrócić
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

        previewView = PreviewView(requireContext())

        return ComposeView(requireContext()).apply {
            setContent {
                CameraScreen(
                    viewModel = viewModel,
                    controller = controller
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = CameraController(
            requireContext(),
            viewLifecycleOwner,
            previewView
        )

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