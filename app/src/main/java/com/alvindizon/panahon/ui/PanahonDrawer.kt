package com.alvindizon.panahon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.design.R
import com.alvindizon.panahon.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PanahonDrawer(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        Image(
            painter = painterResource(com.alvindizon.panahon.R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.open_menu)
        )
        for (dest in destinations) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(id = dest.titleResId),
                style = MaterialTheme.typography.h4,
                modifier = Modifier.clickable {
                    onNavigateToDestination(dest)
                    // Close drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                })
        }
    }
}
