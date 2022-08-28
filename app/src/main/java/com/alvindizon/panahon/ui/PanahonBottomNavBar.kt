package com.alvindizon.panahon.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.alvindizon.panahon.navigation.TopLevelDestination

@Composable
fun PanahonBottomNavBar(
    currentRoute: String?,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    BottomNavigation(contentColor = Color.White) {
        val destNames = destinations.map { stringResource(id = it.titleResId) }
        destinations.forEachIndexed { i, dest ->
            BottomNavigationItem(
                icon = { Icon(imageVector = dest.icon, contentDescription = destNames[i]) },
                label = { Text(text = destNames[i]) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute?.contains(dest.route) ?: false,
                onClick = { onNavigateToDestination(dest) }
            )
        }
    }
}
