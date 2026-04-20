package com.ggs.parkuzpp.main.login

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.ui.RegisterScreen
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ParkUZTheme {
                    RegisterScreen(
                        onNavigateToLogin = {
                            findNavController().navigate(R.id.action_register_to_login)
                        }
                    )
                }
            }
        }
    }
}