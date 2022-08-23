package com.alvindizon.panahon.navigation

import androidx.annotation.StringRes
import com.alvindizon.core.navigation.NavigationDestination

data class TopLevelDestination(
    override val route: String,
    @StringRes val titleResId: Int
): NavigationDestination
