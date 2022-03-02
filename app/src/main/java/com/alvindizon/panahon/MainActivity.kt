package com.alvindizon.panahon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvindizon.panahon.ui.locations.LoadingScreen
import com.alvindizon.panahon.ui.locations.LocationsList
import com.alvindizon.panahon.ui.theme.PanahonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanahonTheme {
                LocationsScreen()
            }
        }
    }
}

@Composable
fun LocationsScreen() {
    val viewModel: MainViewModel = viewModel()

    val context = LocalContext.current

    // need this to prevent infinite loop that happens when using functions
    // ref: https://code.luasoftware.com/tutorials/android/jetpack-compose-load-data-collectasstate-common-mistakes/
    LaunchedEffect(true) {
        viewModel.fetchForecasts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        }
    ) {
        when (val state = viewModel.uiState.collectAsState().value) {
            UiState.Loading -> LoadingScreen()
            is UiState.Success -> {
                LocationsList(
                    locationForecasts = state.list,
                    onLocationClick = {
                        Toast.makeText(
                            context,
                            "Item clicked, location: ${it.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    "Error: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Text(
                text = "No data available",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
