package com.ggs.parkuzpp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.location.UserTriggeredGPSService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.ggs.parkuzpp.location.MapUtils
import com.ggs.parkuzpp.model.Coordinates
import com.ggs.parkuzpp.model.ParkSpot
import com.ggs.parkuzpp.model.ParkingRepository

/**
 * Composable function that renders the main interactive map screen.
 * Handles location permissions, user positioning, map interactions, and parking spot management.
 *
 * @param onNavigateToCamera Callback triggered when the user initiates capturing a photo of their spot.
 */
@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    onNavigateToCamera: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gps = remember { UserTriggeredGPSService(context) }
    val repository = remember { ParkingRepository() }

    val activeSpot by repository.getActiveSpotFlow().collectAsState(initial = null)

    val unknownStreet = stringResource(R.string.map_unknown_street)
    val addressNotFound = stringResource(R.string.map_address_not_found)
    val errorAddress = stringResource(R.string.map_error_address)
    val locatingText = stringResource(R.string.map_locating)
    val loadingLocation = stringResource(R.string.map_loading_location)
    val markerTitle = stringResource(R.string.map_marker_title)

    var currentAddress by remember { mutableStateOf(loadingLocation) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    var googleMap by remember { mutableStateOf<com.google.android.gms.maps.GoogleMap?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(51.938, 15.506), 15f)
    }

    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(isMyLocationEnabled = hasLocationPermission))
    }

    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false))
    }

    suspend fun getAddressFromLocation(lat: Double, lng: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val street = address.thoroughfare ?: unknownStreet
                    val number = address.subThoroughfare ?: ""
                    val city = address.locality ?: ""
                    if (number.isNotEmpty()) "$street $number, $city" else "$street, $city"
                } else addressNotFound
            } catch (_: Exception) {
                errorAddress
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission && activeSpot == null) {
            val location = gps.getCurrentLocation()
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(currentLocation!!, 17f)
                )
            }
        }
    }

    LaunchedEffect(currentLocation, activeSpot) {
        if (activeSpot != null) {
            currentAddress = activeSpot!!.label
        } else if (currentLocation != null) {
            currentAddress = locatingText
            currentAddress = getAddressFromLocation(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )
        }
    }

    LaunchedEffect(activeSpot) {
        activeSpot?.let { spot ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(spot.coordinates.lat, spot.coordinates.lng), 17f
                )
            )
        }
    }

    val markerState = remember(activeSpot) {
        activeSpot?.let {
            MarkerState(
                position = LatLng(it.coordinates.lat, it.coordinates.lng)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            MapEffect(Unit) { map -> googleMap = map }

            markerState?.let {
                Marker(
                    state = it,
                    title = markerTitle,
                    snippet = activeSpot?.label
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Surface(
                    onClick = {
                        if (hasLocationPermission) {
                            scope.launch {
                                val location = gps.getCurrentLocation()
                                location?.let {
                                    val userLatLng = LatLng(it.latitude, it.longitude)
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(userLatLng, 17f)
                                    )
                                }
                            }
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = stringResource(R.string.desc_center_me),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (activeSpot == null) {
                    Surface(
                        onClick = onNavigateToCamera,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(6.dp, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = if (activeSpot == null) stringResource(R.string.map_selected_location) else stringResource(R.string.map_active_parking),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentAddress,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (activeSpot == null) {
                        Button(
                            onClick = {
                                googleMap?.snapshot { bitmap ->
                                    scope.launch {
                                        bitmap?.let {
                                            val uri = MapUtils.saveBitmapToFile(context, it)
                                            val newSpot = ParkSpot(
                                                active = true,
                                                label = currentAddress,
                                                photos = listOf(uri.toString()),
                                                coordinates = Coordinates(
                                                    lat = cameraPositionState.position.target.latitude,
                                                    lng = cameraPositionState.position.target.longitude
                                                )
                                            )
                                            repository.saveParkingSpot(newSpot)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(stringResource(R.string.map_btn_park))
                        }
                    } else {
                        Button(
                            onClick = {
                                scope.launch {
                                    repository.deactivatePreviousSpots()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(stringResource(R.string.map_btn_end_parking))
                        }
                    }
                }
            }
        }
    }
}