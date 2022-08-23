package com.alvindizon.panahon.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.navigation.PanahonNavHost


@Composable
fun PanahonApp(
    activity: ComponentActivity,
    appState: PanahonAppState = rememberPanahonAppState()
) {
    PanahonTheme {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            ),
            scaffoldState = appState.scaffoldState,
            drawerContent = {
                PanahonDrawer(
                    scaffoldState = appState.scaffoldState,
                    scope = appState.coroutineScope,
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigate
                )
            }
        ) {
            PanahonNavHost(
                navController = appState.navController,
                scaffoldState = appState.scaffoldState,
                scope = appState.coroutineScope,
                context = activity,
                finishActivity = { activity.finish() },
                popBackStack = { appState.onBackClick() }
            )
        }
    }
}
