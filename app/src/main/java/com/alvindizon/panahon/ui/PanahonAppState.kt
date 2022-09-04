package com.alvindizon.panahon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun rememberPanahonAppState(
    navController: NavHostController = rememberNavController()
): PanahonAppState {
    return remember { PanahonAppState(navController) }
}

@Stable
class PanahonAppState(val navController: NavHostController) {
    fun onBackClick() {
        navController.popBackStack()
    }
}
