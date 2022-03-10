package com.alvindizon.panahon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alvindizon.panahon.ui.locations.LocationsScreen
import com.alvindizon.panahon.ui.search.SearchScreen
import com.alvindizon.panahon.ui.theme.PanahonTheme
import com.alvindizon.panahon.viewmodel.LocationScreenViewModel
import com.alvindizon.panahon.viewmodel.SearchLocationViewModel
import dagger.hilt.android.AndroidEntryPoint

enum class Screens {
    Locations,
    Search
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PanahonTheme {
                PanahonNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun PanahonNavHost(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = Screens.Locations.name) {
        composable(Screens.Locations.name) {
            val viewModel = hiltViewModel<LocationScreenViewModel>()
            LocationsScreen(viewModel) {
                navController.navigate(Screens.Search.name)
            }
        }
        composable(Screens.Search.name) {
            val viewModel = hiltViewModel<SearchLocationViewModel>()
            SearchScreen(
                viewModel,
                onSearchResultClicked = {
                    Toast.makeText(
                        context,
                        "Search result clicked: ${it.locationName}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onUpButtonClicked = {
                    navController.popBackStack()
                })
        }
    }
}
