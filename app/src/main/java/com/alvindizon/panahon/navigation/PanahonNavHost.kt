package com.alvindizon.panahon.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alvindizon.features.settings.navigation.SettingsNavigation
import com.alvindizon.features.settings.navigation.settingsGraph
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.details.navigation.detailsGraph
import com.alvindizon.panahon.locations.navigation.LocationsNavigation
import com.alvindizon.panahon.locations.navigation.locationsGraph
import com.alvindizon.panahon.searchlocation.navigation.SearchNavigation
import com.alvindizon.panahon.searchlocation.navigation.searchGraph

@Composable
fun PanahonNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    startDestination: String = DetailsNavigation.route,
    popBackStack: () -> Unit,
) {
    Box(modifier = modifier) {
        NavHost(navController = navController, startDestination = startDestination) {
            locationsGraph(
                onLocationClick = {
                    navController.navigate(
                        DetailsNavigation.createNavigationRoute(
                            it.name, it.latitude, it.longitude
                        )
                    )
                },
                onUpButtonClicked = popBackStack,
                onSearchIconClick = { navController.navigate(SearchNavigation.route) },
                nestedGraphs = {
                    searchGraph(onUpButtonClicked = popBackStack, onSearchResultClicked = {
                        navController.navigate(
                            DetailsNavigation.createNavigationRoute(it.locationName, it.lat, it.lon)
                        )
                    }, onLocationFound = {
                        navController.navigate(
                            DetailsNavigation.createNavigationRoute(it.locationName, it.latitude, it.longitude)
                        )
                    })
                }
            )
            searchGraph(onUpButtonClicked = popBackStack, onSearchResultClicked = {
                navController.navigate(
                    DetailsNavigation.createNavigationRoute(it.locationName, it.lat, it.lon)
                )
            }, onLocationFound = {
                navController.navigate(
                    DetailsNavigation.createNavigationRoute(it.locationName, it.latitude, it.longitude)
                )
            })
            detailsGraph(
                onSettingsIconClick = { navController.navigate(SettingsNavigation.route) },
                onNavigationIconClick = { navController.navigate(LocationsNavigation.route) },
                onSnackbarButtonClick =  { context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                )},
                onSearchLinkClick = { navController.navigate(SearchNavigation.route) }
            )
            settingsGraph(onUpButtonClicked = popBackStack)
        }
    }

}
