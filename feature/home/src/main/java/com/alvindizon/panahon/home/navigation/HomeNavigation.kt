package com.alvindizon.panahon.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.ui.HomeScreen

object HomeNavigation : NavigationDestination {
    override val route: String = "home_route"
}

fun NavGraphBuilder.homeGraph(
    onLocationFound: (CurrentLocation) -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
    onErrorOkBtnClick: () -> Unit
) {
    composable(route = HomeNavigation.route) {
        HomeScreen(
            viewModel = hiltViewModel(),
            onLocationFound = onLocationFound,
            onSnackbarButtonClick = onSnackbarButtonClick,
            onSearchLinkClick = onSearchLinkClick,
            onErrorOkBtnClick = onErrorOkBtnClick
        )
    }
}
