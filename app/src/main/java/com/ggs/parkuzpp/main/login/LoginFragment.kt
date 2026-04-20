package com.ggs.parkuzpp.main.login

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.ui.LoginScreen
import com.ggs.parkuzpp.ui.theme.ParkUZTheme
import androidx.navigation.fragment.findNavController
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ParkUZTheme() {
                    LoginScreen(
                        onNavigateToMap = {
                            findNavController().navigate(R.id.action_login_to_map)
                        },
                        onNavigateToRegister = {
                            findNavController().navigate(R.id.action_login_to_register)
                        }
                    )
                }
            }
        }
    }
}