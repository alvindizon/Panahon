package com.alvindizon.features.settings.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.alvindizon.core.navigation.NavigationDestination
import com.alvindizon.features.settings.ui.SettingsScreen

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
            route = SettingsNavigation.destination
        ) {
            SettingsScreen(viewModel = hiltViewModel(), onUpButtonClicked = onUpButtonClicked)
        }
    }
}