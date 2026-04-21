package com.ggs.parkuzpp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.camera.CameraController
import com.ggs.parkuzpp.camera.CameraViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToMap = {
                    navController.navigate("main") {
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

        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("camera") {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current

            var hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    hasCameraPermission = isGranted
                }
            )

            LaunchedEffect(Unit) {
                if (!hasCameraPermission) {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            if (hasCameraPermission) {
                val viewModel: CameraViewModel = viewModel()

                val controller = remember {
                    CameraController(context, lifecycleOwner, PreviewView(context)).apply {
                        startCamera()
                    }
                }

                CameraScreen(
                    viewModel = viewModel,
                    controller = controller
                )
            } else {
                Text(text = "Aby użyć aparatu, musisz zezwolić na dostęp do niego.")
            }
        }
    }
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val parkuzOrange = Color(0xFFFF5722)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    fontSize = 24.sp,
                    color = parkuzOrange,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Mój Profil") },
                    selected = false,
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    onClick = { /* TODO: Otwórz profil */ }
                )
                NavigationDrawerItem(
                    label = { Text("Ustawienia") },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = { /* TODO: Otwórz ustawienia */ }
                )
                NavigationDrawerItem(
                    label = { Text("O aplikacji") },
                    selected = false,
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    onClick = { /* TODO: Otwórz info */ }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Wyloguj się") },
                    selected = false,
                    onClick = { onLogout() },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "map",
                        onClick = {
                            if (currentRoute != "map") {
                                bottomNavController.navigate("map") {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "Mapa",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Mapa") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = parkuzOrange,
                            indicatorColor = parkuzOrange
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "history",
                        onClick = {
                            if (currentRoute != "history") {
                                bottomNavController.navigate("history") {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "Historia",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Historia") },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = bottomNavController,
                startDestination = "map",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("map") {
                    MapScreen(
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        // Nawigacja do kamery musi użyć głównego navControllera (nie jest w MainScreen)
                        onNavigateToCamera = { /* Tu używamy zewnętrznego navController, najlepiej przez lambdę z MainScreen, ale na potrzeby uproszczenia zostawiam komentarz */ }
                    )
                }
                composable("history") {
                    HistoryScreen()
                }
            }
        }
    }
}