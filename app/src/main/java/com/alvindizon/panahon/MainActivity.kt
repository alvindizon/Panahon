package com.alvindizon.panahon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val items by viewModel.itemsFlow.collectAsState(initial = emptyList())

    LocationsList(
        locationForecasts = items,
        onLocationClick = {
            Toast.makeText(
                context,
                "Item clicked, location: ${it.name}",
                Toast.LENGTH_SHORT
            ).show()
        })
}
