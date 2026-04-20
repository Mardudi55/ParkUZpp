package com.ggs.parkuzpp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.ui.MapScreen
import com.ggs.parkuzpp.ui.theme.ParkUZTheme

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ParkUZTheme {
                    MapScreen(
                        onNavigateToHistory = {
                            findNavController().navigate(R.id.action_map_to_history)
                        },
                        onNavigateToAccount = {
                            findNavController().navigate(R.id.action_map_to_account)
                        },
                        onNavigateToCamera = {
                            findNavController().navigate(R.id.action_map_to_camera)
                        }
                    )
                }
            }
        }
    }
}