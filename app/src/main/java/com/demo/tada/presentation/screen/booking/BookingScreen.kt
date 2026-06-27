package com.demo.tada.presentation.screen.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.demo.tada.presentation.navigation.Screen
import com.demo.tada.presentation.screen.map.MapEvent
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Booking Detail") })
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(text = "Error: ${uiState.error}")
            }
        } else {
            val booking = uiState.bookingSummary
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(text = "A location name", style = MaterialTheme.typography.titleMedium)
                Text(text = "aqi ${booking?.a?.aqi ?: 0}")
                Text(text = "nickname ${booking?.a?.nickname ?: "none"}")
                Text(text = booking?.a?.address ?: "")

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "B location name", style = MaterialTheme.typography.titleMedium)
                Text(text = "aqi ${booking?.b?.aqi ?: 0}")
                Text(text = "nickname ${booking?.b?.nickname ?: "none"}")
                Text(text = booking?.b?.address ?: "")

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "price", style = MaterialTheme.typography.titleLarge)
                    Text(text = "${booking?.price?.toInt() ?: 0}", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navController.navigate(Screen.History.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "V")
                }
            }
        }
    }
}
