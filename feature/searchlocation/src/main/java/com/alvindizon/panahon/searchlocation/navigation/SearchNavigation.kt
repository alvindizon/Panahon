package com.alvindizon.panahon.searchlocation.navigation

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
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.panahon.data.location.model.CurrentLocation
import com.alvindizon.panahon.data.location.model.SearchResult
import com.alvindizon.panahon.searchlocation.ui.SearchScreen

object SearchNavigation : NavigationDestination {
    override val route: String = "search_route"
    override val destination: String = "search_destination"
}

fun NavGraphBuilder.searchGraph(
    onUpButtonClicked: () -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit,
    onLocationFound: (CurrentLocation) -> Unit
) {
    composable(
        route = SearchNavigation.route,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    300, easing = LinearEasing
                )
            ) + slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            )
        }
    ) {
        SearchScreen(
            viewModel = hiltViewModel(),
            onUpButtonClicked = onUpButtonClicked,
            onSearchResultClicked = onSearchResultClicked,
            onLocationFound = onLocationFound
        )
    }
}
