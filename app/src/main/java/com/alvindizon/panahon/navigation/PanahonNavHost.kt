package com.alvindizon.panahon.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.details.navigation.detailsGraph
import com.alvindizon.panahon.home.navigation.HomeNavigation
import com.alvindizon.panahon.home.navigation.homeGraph
import com.alvindizon.panahon.locations.navigation.LocationsNavigation
import com.alvindizon.panahon.locations.navigation.locationsGraph
import com.alvindizon.panahon.searchlocation.navigation.SearchNavigation
import com.alvindizon.panahon.searchlocation.navigation.searchGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost

@Composable
fun PanahonNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    startDestination: String = HomeNavigation.route,
    finishActivity: () -> Unit,
    popBackStack: () -> Unit,
) {
    Box(modifier = modifier) {
        AnimatedNavHost(navController = navController, startDestination = startDestination){
            homeGraph(
                onLocationFound = {
                    navController.navigate(
                        DetailsNavigation.createNavigationRoute(
                            it.locationName, it.latitude, it.longitude
                        )
                    )
                },
                onSnackbarButtonClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                },
                onSearchLinkClick = { navController.navigate(SearchNavigation.route) },
                onErrorOkBtnClick = finishActivity,
                nestedGraphs = { searchGraph(onUpButtonClicked = popBackStack) }
            )
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
                nestedGraphs = { searchGraph(onUpButtonClicked = popBackStack) }
            )
            searchGraph(onUpButtonClicked = popBackStack)
            detailsGraph(
                onSearchIconClick = { navController.navigate(SearchNavigation.route) },
                onNavigationIconClick = { navController.navigate(LocationsNavigation.route) }
            )
        }
    }

}
