package com.alvindizon.panahon.locations.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.ui.LocationsScreen

object LocationsNavigation : NavigationDestination {
    override val route: String = "locations_route"
}

fun NavGraphBuilder.locationsGraph(
    onLocationClick: (LocationForecast) -> Unit,
    onUpButtonClicked: () -> Unit,
) {
    composable(route = LocationsNavigation.route) {
        LocationsScreen(
            viewModel = hiltViewModel(),
            onLocationClick = onLocationClick,
            onUpButtonClicked = onUpButtonClicked
        )
    }
}
