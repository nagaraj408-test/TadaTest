package com.demo.tada.presentation.screen.nickname

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NicknameScreen(
    navController: NavHostController,
    type: String?,
    mapBackStackEntry: NavBackStackEntry
) {
    val viewModel: MapViewModel = hiltViewModel(mapBackStackEntry)
    val uiState by viewModel.uiState.collectAsState()

    val location = if (type == "A") uiState.aLocation else uiState.bLocation
    var nickname by remember { mutableStateOf(location?.nickname ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = if (type == "A") "A location name" else "B location name") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "aqi ${location?.aqi ?: 0}")
            Text(text = location?.address ?: "Unknown Address")
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = nickname,
                onValueChange = {
                    if (it.length <= 20) {
                        nickname = it
                    }
                },
                label = { Text("nickname") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    type?.let {
                        viewModel.setNickname(it, nickname)
                    }
                    navController.popBackStack()
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
