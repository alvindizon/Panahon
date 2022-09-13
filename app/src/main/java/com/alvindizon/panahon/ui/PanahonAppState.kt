package com.alvindizon.panahon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


@Composable
fun rememberPanahonAppState(
    navController: NavHostController = rememberAnimatedNavController()
): PanahonAppState {
    return remember { PanahonAppState(navController) }
}

@Stable
class PanahonAppState(val navController: NavHostController) {
    fun onBackClick() {
        navController.popBackStack()
    }
}
