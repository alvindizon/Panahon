package com.alvindizon.panahon.searchlocation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.searchlocation.model.SearchResult
import com.alvindizon.panahon.searchlocation.model.CurrentLocation
import com.alvindizon.panahon.searchlocation.ui.SearchScreen
import androidx.navigation.compose.composable

object SearchNavigation : NavigationDestination {
    override val route: String = "search_route"
    override val destination: String = "search_destination"
}

fun NavGraphBuilder.searchGraph(
    onUpButtonClicked: () -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit,
    onLocationFound: (CurrentLocation) -> Unit
) {
    composable(route = SearchNavigation.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
    ) {
        SearchScreen(
            viewModel = hiltViewModel(),
            onUpButtonClicked = onUpButtonClicked,
            onSearchResultClicked = onSearchResultClicked,
            onLocationFound = onLocationFound
        )
    }
}
