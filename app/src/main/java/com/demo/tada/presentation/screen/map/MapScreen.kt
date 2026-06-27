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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import kotlin.time.Duration.Companion.seconds
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(37.5665, 126.9780),
            15f
        )
    }

    // Tracs if we already moved to the user initial location
    var hasSnappedToUser by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    // Handles initial location
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted && !hasSnappedToUser) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
                    )
                    hasSnappedToUser = true
                } else {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { freshLocation ->
                            freshLocation?.let {
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                                )
                                hasSnappedToUser = true
                            }
                        }
                }
            }
        }
    }

    // Sync camera target with ViewModel (Pointer Location & AQI)
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position.target }
            .distinctUntilChanged()
            .collectLatest { target ->
                if (target.latitude != 0.0 || target.longitude != 0.0) {
                    viewModel.onEvent(
                        MapEvent.CameraMoved(
                            latitude = target.latitude,
                            longitude = target.longitude
                        )
                    )
                }
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
                properties = MapProperties(
                    isMyLocationEnabled = permissionsState.allPermissionsGranted,
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            )

            // My Location Button
            if (permissionsState.allPermissionsGranted) {
                FloatingActionButton(
                    onClick = {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(location.latitude, location.longitude),
                                        15f
                                    )
                                )
                            } else {
                                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                    .addOnSuccessListener { freshLocation ->
                                        freshLocation?.let {
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
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 140.dp, end = 16.dp),
                    containerColor = Color.White,
                    contentColor = TadaBlue,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "My Location")
                }
            }

            // Marker at Center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Address Tooltip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = uiState.currentAddress,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.icon_pin),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
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
                        .height(IntrinsicSize.Min)
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
                            .width(112.dp)
                            .fillMaxHeight(),
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
