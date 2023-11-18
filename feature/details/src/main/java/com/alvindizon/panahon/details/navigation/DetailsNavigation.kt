package com.alvindizon.panahon.details.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.details.ui.DetailsScreen
import androidx.navigation.compose.composable

object DetailsNavigation : NavigationDestination {
    const val locationArg = "location"
    const val latitudeArg = "latitude"
    const val longitudeArg = "longitude"

    override val route: String =
        "details_route?location={$locationArg}?latitude={$latitudeArg}?longitude={$longitudeArg}"
    override val destination: String = "details_destination"

    fun createNavigationRoute(location: String, latitude: String, longitude: String): String =
        "details_route?location=$location?latitude=$latitude?longitude=$longitude"
}

fun NavGraphBuilder.detailsGraph(
    onSettingsIconClick: () -> Unit,
    onNavigationIconClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
    onSnackbarButtonClick: () -> Unit
) {
    composable(
        route = DetailsNavigation.route,
        arguments = listOf(
            navArgument(DetailsNavigation.locationArg) {
                nullable = true
                type = NavType.StringType
            },
            navArgument(DetailsNavigation.latitudeArg) {
                nullable = true
                type = NavType.StringType
            },
            navArgument(DetailsNavigation.longitudeArg) {
                nullable = true
                type = NavType.StringType
            },
        )
    ) {
        DetailsScreen(
            viewModel = hiltViewModel(),
            onSettingsIconClick = onSettingsIconClick,
            onNavigationIconClick = onNavigationIconClick,
            onSnackbarButtonClick = onSnackbarButtonClick,
            onSearchLinkClick = onSearchLinkClick
        )
    }
}
