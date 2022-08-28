package com.alvindizon.panahon.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.locations.navigation.LocationsNavigation
import com.alvindizon.panahon.navigation.TopLevelDestination
import com.alvindizon.panahon.searchlocation.navigation.SearchNavigation

@Composable
fun rememberPanahonAppState(
    navController: NavHostController = rememberNavController()
): PanahonAppState {
    return remember { PanahonAppState(navController) }
}

class PanahonAppState(
    val navController: NavHostController
) {

    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            route = LocationsNavigation.route,
            titleResId = com.alvindizon.panahon.locations.R.string.locations,
            icon = Icons.Filled.Bookmarks
        ),
        TopLevelDestination(
            route = SearchNavigation.route,
            titleResId = com.alvindizon.panahon.design.R.string.search,
            icon = Icons.Filled.Search
        ),
        TopLevelDestination(
            route = DetailsNavigation.route,
            titleResId = com.alvindizon.panahon.details.R.string.details,
            icon = Icons.Filled.Details
        ),
    )

    fun navigate(destination: NavigationDestination, route: String? = null) {
        if (destination is TopLevelDestination) {
            navController.navigate(route ?: destination.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) { saveState = true }
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        } else {
            navController.navigate(route ?: destination.route)
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
