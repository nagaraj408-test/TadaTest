package com.demo.tada.presentation.screen.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.demo.tada.domain.model.LocationInfo
import com.demo.tada.presentation.navigation.Screen
import com.demo.tada.presentation.screen.map.MapEvent
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel
import com.demo.tada.ui.theme.TadaYellow
import com.demo.tada.ui.theme.TadaBlue

@Composable
fun BookingScreen(
    navController: NavHostController,
    mapBackStackEntry: NavBackStackEntry
) {
    val viewModel: MapViewModel = hiltViewModel(mapBackStackEntry)
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        viewModel.onEvent(MapEvent.Reset)
        navController.popBackStack(Screen.Map.route, false)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

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

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${uiState.error}", color = Color.Red)
            }
        } else {
            val booking = uiState.bookingSummary
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 24.dp)
            ) {
                LocationSummary(
                    type = "A",
                    location = booking?.a
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color(0xFFEEEEEE)
                )

                LocationSummary(
                    type = "B",
                    location = booking?.b
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "price",
                        fontSize = 24.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${booking?.price?.toInt() ?: 0}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.History.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TadaYellow,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "V",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
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

@Composable
fun LocationSummary(
    type: String,
    location: LocationInfo?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = type,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "location name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 52.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "aqi",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Text(
                text = "${location?.aqi ?: 0}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 52.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "nickname",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Text(
                text = location?.nickname ?: "none",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
