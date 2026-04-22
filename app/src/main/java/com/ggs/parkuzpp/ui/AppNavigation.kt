package com.ggs.parkuzpp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ggs.parkuzpp.ui.theme.ParkUZPrimaryOrange
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


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    fontSize = 24.sp,
                    color = ParkUZPrimaryOrange,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Mój Profil") },
                    selected = false,
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    onClick = { /* TODO: Otwórz profil */ }
                )
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
                // UŻYWAMY NASZEGO CUSTOMOWEGO PASKA DOLNEGO
                CustomBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
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
                        onNavigateToCamera = { /* Nawigacja kamery */ }
                    )
                }
                composable("history") {
                    HistoryScreen(
                        onOpenMenu = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

// =========================================
// CUSTOMOWE KOMPONENTY PASKA DOLNEGO
// =========================================

@Composable
fun CustomBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val parkuzOrange = Color(0xFFFF5722)

    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomNavItem(
                text = "Mapa",
                icon = Icons.Default.Map,
                isSelected = currentRoute == "map",
                activeColor = parkuzOrange,
                onClick = { onNavigate("map") }
            )
            CustomNavItem(
                text = "Historia",
                icon = Icons.Default.History,
                isSelected = currentRoute == "history",
                activeColor = parkuzOrange,
                onClick = { onNavigate("history") }
            )
        }
    }
}

@Composable
fun CustomNavItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) activeColor else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Gray

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 36.dp, vertical = 8.dp), // Zapewnia szeroki, pomarańczowy "kafelek"
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}