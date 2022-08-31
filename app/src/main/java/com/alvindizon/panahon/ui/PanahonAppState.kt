package com.alvindizon.panahon.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.home.navigation.HomeNavigation
import com.alvindizon.panahon.locations.navigation.LocationsNavigation
import com.alvindizon.panahon.navigation.TopLevelDestination
import com.alvindizon.panahon.searchlocation.navigation.SearchNavigation


@Composable
fun rememberPanahonAppState(
    navController: NavHostController = rememberNavController()
): PanahonAppState {
    return remember { PanahonAppState(navController) }
}

@Stable
class PanahonAppState(val navController: NavHostController) {

    val currentNavDestination: NavDestination?
        @Composable
        get() = navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar: Boolean
        @Composable
        get() = currentNavDestination?.route != HomeNavigation.destination

    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            route = DetailsNavigation.route,
            destination = DetailsNavigation.destination,
            titleResId = com.alvindizon.panahon.details.R.string.weather,
            icon = Icons.Filled.WbSunny
        ),
        TopLevelDestination(
            route = LocationsNavigation.route,
            destination = LocationsNavigation.destination,
            titleResId = com.alvindizon.panahon.locations.R.string.locations,
            icon = Icons.Filled.Bookmarks
        ),
        TopLevelDestination(
            route = SearchNavigation.route,
            destination = SearchNavigation.destination,
            titleResId = com.alvindizon.panahon.design.R.string.search,
            icon = Icons.Filled.Search
        ),
    )

    fun navigate(destination: NavigationDestination, route: String? = null) {
        if (destination is TopLevelDestination) {
            navController.navigate(route ?: destination.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
            }
        } else {
            navController.navigate(route ?: destination.route)
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
