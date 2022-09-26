package com.alvindizon.features.settings.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.features.settings.ui.SettingsScreen
import com.google.accompanist.navigation.animation.composable

object SettingsNavigation : NavigationDestination {
    override val route: String = "settings_route"
    override val destination: String = "settings_destination"
}


fun NavGraphBuilder.settingsGraph(
    onUpButtonClicked: () -> Unit
) {
    navigation(
        route = SettingsNavigation.route,
        startDestination = SettingsNavigation.destination
    ) {
        composable(
            route = SettingsNavigation.destination,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
        ) {
            SettingsScreen(onUpButtonClicked = onUpButtonClicked)
        }
    }
}