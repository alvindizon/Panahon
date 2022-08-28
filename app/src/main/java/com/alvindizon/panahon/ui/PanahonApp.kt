package com.alvindizon.panahon.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.home.navigation.HomeNavigation
import com.alvindizon.panahon.navigation.PanahonNavHost


@Composable
fun PanahonApp(
    activity: ComponentActivity,
    appState: PanahonAppState = rememberPanahonAppState()
) {
    var showBottomBar by rememberSaveable{ mutableStateOf(true) }
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    showBottomBar = navBackStackEntry?.destination?.route != HomeNavigation.route
    PanahonTheme {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical)
            ),
            bottomBar = {
                if (showBottomBar) PanahonBottomNavBar(
                    navController = appState.navController,
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigate
                )
            }
        ) {
            PanahonNavHost(
                navController = appState.navController,
                context = activity,
                finishActivity = { activity.finish() },
                popBackStack = { appState.onBackClick() }
            )
        }
    }
}
