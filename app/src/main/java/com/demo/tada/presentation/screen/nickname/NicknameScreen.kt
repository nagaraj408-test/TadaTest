package com.demo.tada.presentation.screen.nickname

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
import com.demo.tada.presentation.screen.map.viewmodel.MapViewModel
import com.demo.tada.ui.theme.TadaYellow
import com.demo.tada.ui.theme.TadaBlue

@Composable
fun NicknameScreen(
    navController: NavHostController,
    type: String?,
    mapBackStackEntry: NavBackStackEntry
) {
    val viewModel: MapViewModel = hiltViewModel(mapBackStackEntry)
    val uiState by viewModel.uiState.collectAsState()

    val location = if (type == "A") uiState.aLocation else uiState.bLocation
    
    // Use a local state that updates instantly
    var textState by remember { mutableStateOf(location?.nickname ?: "") }

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

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = type ?: "A",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = location?.address ?: "location name",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "aqi",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.W700
                )
                Text(
                    text = "${location?.aqi ?: 0}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Pushes the input and button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = textState,
                onValueChange = {
                    if (it.length <= 20) {
                        textState = it
                    }
                },
                placeholder = { 
                    Text(
                        "nickname", 
                        color = Color.Black, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFFEEEEEE),
                    unfocusedIndicatorColor = Color(0xFFEEEEEE),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    type?.let {
                        viewModel.setNickname(it, textState)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
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
            
            Spacer(modifier = Modifier.height(8.dp))
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
