package com.alvindizon.panahon.locations.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.ui.LocationsScreen
import com.google.accompanist.navigation.animation.composable

object LocationsNavigation : NavigationDestination {
    override val route: String = "locations_route"
    override val destination: String = "locations_destination"
}


fun NavGraphBuilder.locationsGraph(
    onLocationClick: (LocationForecast) -> Unit,
    onUpButtonClicked: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = LocationsNavigation.route,
        startDestination = LocationsNavigation.destination
    ) {
        composable(
            route = LocationsNavigation.destination,
            enterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
        ) {
            LocationsScreen(
                viewModel = hiltViewModel(),
                onLocationClick = onLocationClick,
                onUpButtonClicked = onUpButtonClicked
            )
        }
        nestedGraphs()
    }
}
