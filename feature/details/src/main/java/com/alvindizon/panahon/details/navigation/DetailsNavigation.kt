package com.alvindizon.panahon.details.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.details.ui.DetailsScreen

object DetailsNavigation: NavigationDestination {
    const val locationArg = "location"
    const val latitudeArg = "latitude"
    const val longitudeArg = "longitude"

    override val route: String = "details_route/{$locationArg}/{$latitudeArg}/{$longitudeArg}"
    override val destination: String = "details_destination"

    fun createNavigationRoute(location: String, latitude: String, longitude: String): String =
        "details_route/$location/$latitude/$longitude"
}

fun NavGraphBuilder.detailsGraph(
    onSearchIconClick: () -> Unit
) {
    composable(route = DetailsNavigation.route,
    arguments =  listOf(
        navArgument(DetailsNavigation.locationArg) { type = NavType.StringType },
        navArgument(DetailsNavigation.latitudeArg) { type = NavType.StringType },
        navArgument(DetailsNavigation.longitudeArg) { type = NavType.StringType }
    )) {
        DetailsScreen(
            viewModel = hiltViewModel(),
            location = it.arguments?.getString(DetailsNavigation.locationArg)!!,
            latitude = it.arguments?.getString(DetailsNavigation.latitudeArg)!!,
            longitude = it.arguments?.getString(DetailsNavigation.longitudeArg)!!,
            onSearchIconClick = onSearchIconClick
        )
    }
}
