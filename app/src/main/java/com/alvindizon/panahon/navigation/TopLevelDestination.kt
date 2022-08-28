package com.alvindizon.panahon.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.alvindizon.core.navigation.NavigationDestination

data class TopLevelDestination(
    override val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
): NavigationDestination
