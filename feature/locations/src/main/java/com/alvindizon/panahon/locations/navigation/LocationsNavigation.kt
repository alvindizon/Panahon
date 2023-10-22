package com.alvindizon.panahon.locations.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        startDestination = LocationsNavigation.destination,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        }
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
