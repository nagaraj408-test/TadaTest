package com.demo.tada.presentation.screen.cached

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel
import com.demo.tada.ui.theme.TadaBlue

@Composable
fun CachedLocationsScreen(
    navController: NavHostController,
    type: String?,
    mapBackStackEntry: NavBackStackEntry,
    viewModel: CachedLocationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val mapViewModel: MapViewModel = hiltViewModel(mapBackStackEntry)

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
                text = "Select Location",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.locations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No cached locations found")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.locations) { location ->
                    CachedLocationItem(location) {
                        type?.let { t ->
                            mapViewModel.setLocationFromCache(t, location)
                            navController.popBackStack()
                        }
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
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

@Composable
fun CachedLocationItem(
    location: CachedLocation,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = location.address,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "AQI: ${location.aqi}",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
