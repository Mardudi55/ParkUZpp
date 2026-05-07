package com.ggs.parkuzpp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.camera.CameraController
import com.ggs.parkuzpp.camera.CameraViewModel
import kotlinx.coroutines.launch

/**
 * Defines the main navigation graph for the application.
 * Handles the routing between authentication, the main app experience, and camera functionalities.
 *
 * @param isDarkTheme Indicates if the dark theme is currently active.
 * @param onThemeChange Callback to toggle the theme state.
 * @param currentLanguage The currently selected app language code.
 * @param onLanguageChange Callback to update the app's language.
 */
@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val authRepository = remember { com.ggs.parkuzpp.auth.AuthRepository() }

    NavHost(
        navController = navController,
        startDestination = if (authRepository.isUserLoggedIn()) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onNavigateToMap = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register")}
            )
        }

        composable("register") {
            RegisterScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("account") {
            MenuScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onNavigate = { route -> navController.navigate(route) },
                onLogout = { }
            )
        }

        composable("main") {
            MainScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onLogout = {
                    authRepository.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToCamera = { navController.navigate("camera") }
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
                    controller = controller,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Text(
                    text = stringResource(R.string.camera_permission_denied),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/**
 * The core screen of the application containing a bottom navigation bar, top app bar, and a navigation drawer.
 * Hosts a nested navigation controller for switching between map, history, and account views.
 */
@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigateToCamera: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || currentRoute != "map",
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                MenuScreen(
                    currentRoute = currentRoute,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        if (currentRoute != route) {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    onOpenMenu = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                CustomBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
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
                        onNavigateToCamera = onNavigateToCamera
                    )
                }
                composable("history") {
                    HistoryScreen(
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        onNavigateToMap = { bottomNavController.navigate("map") }
                    )
                }
                composable("password") {
                    AccountScreen(
                        onBack = { bottomNavController.popBackStack() }
                    )
                }
                composable("settings") {
                    SettingsScreen()
                }

            }
        }
    }
}

/**
 * A custom top application bar displaying a menu button, the application name, and the logo.
 *
 * @param onOpenMenu Callback triggered when the hamburger menu icon is clicked.
 */
@Composable
fun CustomTopAppBar(onOpenMenu: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onOpenMenu) {
            Icon(
                Icons.Default.Menu,
                contentDescription = stringResource(R.string.menu_desc),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_logo_withoutbg),
            contentDescription = stringResource(R.string.logo_desc),
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            tint = Color.Unspecified
        )
    }
}

/**
 * A custom bottom navigation bar for switching between main application sections.
 *
 * @param currentRoute The currently active route within the nested navigation controller.
 * @param onNavigate Callback triggered when a navigation item is selected.
 */
@Composable
fun CustomBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
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
                text = stringResource(R.string.nav_map),
                icon = Icons.Default.Map,
                isSelected = currentRoute == "map",
                onClick = { onNavigate("map") }
            )
            CustomNavItem(
                text = stringResource(R.string.nav_history),
                icon = Icons.Default.History,
                isSelected = currentRoute == "history",
                onClick = { onNavigate("history") }
            )
        }
    }
}

/**
 * A stylized single item within the bottom navigation bar.
 *
 * @param text The label for the navigation item.
 * @param icon The visual icon for the navigation item.
 * @param isSelected Determines if the item is currently active, changing its visual state.
 * @param onClick Callback triggered when the item is pressed.
 */
@Composable
fun CustomNavItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 36.dp, vertical = 8.dp),
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