package com.alvindizon.panahon.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navigation
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.ui.HomeScreen

object HomeNavigation : NavigationDestination {
    override val route: String = "home_route"
    override val destination: String = "home_destination"
}

fun NavGraphBuilder.homeGraph(
    onLocationFound: (CurrentLocation) -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
    onErrorOkBtnClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = HomeNavigation.route,
        startDestination = HomeNavigation.destination
    ) {
        composable(route = HomeNavigation.destination) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onLocationFound = onLocationFound,
                onSnackbarButtonClick = onSnackbarButtonClick,
                onSearchLinkClick = onSearchLinkClick,
                onErrorOkBtnClick = onErrorOkBtnClick
            )
        }
        nestedGraphs()
    }
}
