package com.ggs.parkuzpp.ui

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ggs.parkuzpp.main.camera.CameraController
import com.ggs.parkuzpp.main.camera.CameraViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost zastępuje Twój FragmentContainerView
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onNavigateToMap = {
                    // popUpTo czyści stos, żeby cofnięcie z mapy nie wracało do logowania
                    navController.navigate("map") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("map") {
            MapScreen(
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToAccount = { navController.navigate("account") },
                onNavigateToCamera = { navController.navigate("camera") }
            )
        }

        composable("account") {
            AccountScreen()
        }

        composable("history") {
            // Zakładam, że stworzyłeś HistoryScreen na wzór AccountScreen
            HistoryScreen()
        }

        composable("camera") {
            // Konfiguracja kamery w czystym Compose
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val viewModel: CameraViewModel = viewModel() // Compose automatycznie zarządza ViewModelem

            // Inicjalizujemy kontroler i zapamiętujemy go, by nie tworzył się na nowo
            val controller = remember {
                CameraController(context, lifecycleOwner, PreviewView(context)).apply {
                    startCamera() // Automatyczny start, jeśli uprawnienia są nadane
                }
            }

            CameraScreen(
                viewModel = viewModel,
                controller = controller
            )
        }
    }
}