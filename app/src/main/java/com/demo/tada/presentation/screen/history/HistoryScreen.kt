package com.demo.tada.presentation.screen.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.demo.tada.presentation.screen.history.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "History Screen") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(text = "Total Count", style = MaterialTheme.typography.labelMedium)
                    Text(text = "${uiState.totalCount}", style = MaterialTheme.typography.titleLarge)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(text = "Total Price", style = MaterialTheme.typography.labelMedium)
                    Text(text = "${uiState.totalPrice.toInt()}", style = MaterialTheme.typography.titleLarge)
                }
            }

            HorizontalDivider()

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(text = "Error: ${uiState.error}")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.bookings) { book ->
                        ListItem(
                            headlineContent = {
                                Text(text = "A ${book.a.nickname ?: book.a.address}")
                            },
                            supportingContent = {
                                Text(text = "B ${book.b.nickname ?: book.b.address}")
                            },
                            trailingContent = {
                                Text(text = "${book.price.toInt()}")
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
