package com.alvindizon.panahon.locations.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.ui.LocationsScreen

object LocationsNavigation : NavigationDestination {
    override val route: String = "locations_route"
    override val destination: String = "locations_destination"
}


fun NavGraphBuilder.locationsGraph(
    onLocationClick: (LocationForecast) -> Unit,
    onUpButtonClicked: () -> Unit,
    onSearchIconClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = LocationsNavigation.route,
        startDestination = LocationsNavigation.destination
    ) {
        composable(route = LocationsNavigation.destination) {
            LocationsScreen(
                viewModel = hiltViewModel(),
                onLocationClick = onLocationClick,
                onUpButtonClicked = onUpButtonClicked,
                onSearchIconClick = onSearchIconClick
            )
        }
        nestedGraphs()
    }
}
