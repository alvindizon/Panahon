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
                WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical)
            )
        ) { padding ->
            PanahonNavHost(
                modifier = Modifier.padding(padding),
                navController = appState.navController,
                context = activity,
                popBackStack = { appState.onBackClick() }
            )
        }
    }
}
