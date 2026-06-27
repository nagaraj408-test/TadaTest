package com.demo.tada.presentation.screen.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import com.demo.tada.presentation.navigation.Screen
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationPermissionState: PermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(
                    37.5665,
                    126.9780
                ),
                15f
            )
        }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            15f
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(
        cameraPositionState.isMoving
    ) {

        if (!cameraPositionState.isMoving) {

            val target = cameraPositionState.position.target

            viewModel.onEvent(
                MapEvent.CameraMoved(
                    latitude = target.latitude,
                    longitude = target.longitude
                )
            )
        }
    }

    LaunchedEffect(uiState.navigateToBooking) {
        if (uiState.navigateToBooking) {
            navController.navigate(Screen.Booking.route)
            viewModel.onEvent(MapEvent.BookingNavigationHandled)
        }
    }

    LaunchedEffect(uiState.navigateToNickname) {
        uiState.navigateToNickname?.let { type ->
            navController.navigate(Screen.Nickname.createRoute(type))
            viewModel.onEvent(MapEvent.NicknameNavigationHandled)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted)
        ) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = cameraPositionState.position.target
                )
            )
        }

        Text(
            text = "aqi ${uiState.currentAqi}",
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .padding(
                    top = 50.dp,
                    end = 16.dp
                )
        )

        Text(
            text = "📍",
            modifier = Modifier
                .align(
                    Alignment.Center
                )
        )

        Row(
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(MapEvent.AClicked)
                        }
                ) {

                    Text(
                        text =
                            uiState.aLocation?.nickname
                                ?: uiState.aLocation?.address
                                ?: "A",
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(MapEvent.BClicked)
                        }
                ) {

                    Text(
                        text =
                            uiState.bLocation?.nickname
                                ?: uiState.bLocation?.address
                                ?: "B",
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Button(
                onClick = {

                    if (
                        uiState.aLocation != null &&
                        uiState.bLocation != null
                    ) {
                        viewModel.onEvent(MapEvent.BookClicked)
                    } else {

                        viewModel.onEvent(
                            MapEvent.SetLocation
                        )
                    }
                },
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
            ) {

                Text(
                    text = viewModel.getButtonText()
                )
            }
        }
    }
}
