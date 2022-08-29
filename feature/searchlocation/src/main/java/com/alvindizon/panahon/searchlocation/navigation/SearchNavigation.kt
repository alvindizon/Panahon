package com.alvindizon.panahon.searchlocation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.searchlocation.search.SearchScreen

object SearchNavigation : NavigationDestination {
    override val route: String = "search_route"
    override val destination: String = "search_destination"
}

fun NavGraphBuilder.searchGraph(
    onUpButtonClicked: () -> Unit
) {
    composable(route = SearchNavigation.route) {
        SearchScreen(viewModel = hiltViewModel(), onUpButtonClicked = onUpButtonClicked)
    }
}
