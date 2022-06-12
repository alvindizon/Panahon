package com.alvindizon.panahon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.details.ui.DetailsScreen
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel
import com.alvindizon.panahon.locations.ui.LocationsScreen
import com.alvindizon.panahon.locations.viewmodel.LocationScreenViewModel
import com.alvindizon.panahon.searchlocation.search.SearchScreen
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationViewModel
import dagger.hilt.android.AndroidEntryPoint

enum class Screens {
    Locations,
    Search,
    Details
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
    NavHost(navController = navController, startDestination = Screens.Locations.name) {
        composable(Screens.Locations.name) {
            val viewModel = hiltViewModel<LocationScreenViewModel>()
            LocationsScreen(viewModel = viewModel,
                title = stringResource(id = R.string.app_name),
                onSearchIconClicked = {
                    navController.navigate(Screens.Search.name)
                },
                onLocationClick = {
                    navController.navigate(
                        "${Screens.Details.name}/${it.name}/${it.latitude}/${it.longitude}"
                    )
                }
            )
        }
        composable(Screens.Search.name) {
            val viewModel = hiltViewModel<SearchLocationViewModel>()
            SearchScreen(
                viewModel = viewModel,
                onUpButtonClicked = {
                    navController.popBackStack()
                }
            )
        }
        composable("${Screens.Details.name}/{location}/{latitude}/{longitude}", arguments = listOf(
            navArgument("location") { type = NavType.StringType },
            navArgument("latitude") { type = NavType.StringType },
            navArgument("longitude") { type = NavType.StringType }
        )) {
            val viewModel = hiltViewModel<DetailsScreenViewModel>()
            DetailsScreen(
                viewModel = viewModel,
                location = it.arguments!!.getString("location")!!,
                latitude = it.arguments!!.getString("latitude")!!,
                longitude = it.arguments!!.getString("longitude")!!
            )
        }
    }
}
