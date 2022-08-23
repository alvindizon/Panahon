package com.alvindizon.panahon.ui

import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.locations.navigation.LocationsNavigation
import com.alvindizon.panahon.navigation.TopLevelDestination
import com.alvindizon.panahon.searchlocation.navigation.SearchNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberPanahonAppState(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): PanahonAppState {
    return remember { PanahonAppState(navController, scaffoldState, coroutineScope) }
}

class PanahonAppState(
    val navController: NavHostController,
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope
) {

    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            route = LocationsNavigation.route,
            titleResId = com.alvindizon.panahon.locations.R.string.locations
        ),
        TopLevelDestination(
            route = SearchNavigation.route,
            titleResId = com.alvindizon.panahon.design.R.string.search
        )
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
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
