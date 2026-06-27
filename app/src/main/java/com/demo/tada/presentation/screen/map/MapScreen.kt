package com.demo.tada.presentation.screen.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import com.demo.tada.R
import com.demo.tada.presentation.navigation.Screen
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel
import com.demo.tada.ui.theme.TadaYellow
import com.demo.tada.ui.theme.TadaGray
import com.demo.tada.ui.theme.TadaBlue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(37.5665, 126.9780),
                15f
            )
        }

    // Helper to sync camera and trigger AQI
    val updateCameraAndAqi = { lat: Double, lng: Double ->
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f)
        )
        viewModel.onEvent(MapEvent.CameraMoved(latitude = lat, longitude = lng))
    }

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // Handle initial location and its AQI
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    updateCameraAndAqi(location.latitude, location.longitude)
                } else {
                    // Force current location if lastLocation is null (common for Pune/Real device)
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { freshLocation ->
                            freshLocation?.let {
                                updateCameraAndAqi(it.latitude, it.longitude)
                            }
                        }
                }
            }
        }
    }

    // Regular camera movement updates - simplified and reliable
    LaunchedEffect(cameraPositionState.isMoving) {
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

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Top Safe Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(TadaBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Top Safe Area",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // AQI Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "aqi ",
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = "${uiState.currentAqi}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            )

            // Marker at Center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_pin),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )

                Box(
                    modifier = Modifier
                        .size(4.dp, 12.dp)
                        .background(Color.Black)
                        .align(Alignment.BottomCenter)
                        .padding(top = 28.dp)
                )
            }

            // Bottom UI Section
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(MapEvent.AClicked) },
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = TadaGray)
                        ) {
                            Text(
                                text = uiState.aLocation?.nickname ?: uiState.aLocation?.address ?: "A",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(MapEvent.BClicked) },
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = TadaGray)
                        ) {
                            Text(
                                text = uiState.bLocation?.nickname ?: uiState.bLocation?.address ?: "B",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (uiState.aLocation != null && uiState.bLocation != null) {
                                viewModel.onEvent(MapEvent.BookClicked)
                            } else {
                                viewModel.onEvent(MapEvent.SetLocation)
                            }
                        },
                        modifier = Modifier
                            .width(108.dp) // Slightly wider for text fit
                            .height(104.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TadaYellow,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = viewModel.getButtonText(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Bottom Safe Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(TadaBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bottom Safe Area",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
