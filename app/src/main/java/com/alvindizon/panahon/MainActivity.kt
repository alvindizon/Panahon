package com.alvindizon.panahon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.details.ui.DetailsScreen
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel
import com.alvindizon.panahon.home.ui.HomeScreen
import com.alvindizon.panahon.home.viewmodel.HomeScreenViewModel
import com.alvindizon.panahon.locations.ui.LocationsScreen
import com.alvindizon.panahon.locations.viewmodel.LocationScreenViewModel
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationViewModel
import com.alvindizon.panahon.ui.search.SearchScreen
import dagger.hilt.android.AndroidEntryPoint


enum class Screens(
    val icon: ImageVector
) {
    Locations(icon = Icons.Filled.Bookmarks),
    Search(icon = Icons.Filled.Search),
    Details(icon = Icons.Filled.Details),
    Home(icon = Icons.Filled.Home);

    companion object {
        fun fromRoute(route: String?): Screens =
            when (route?.substringBefore("/")) {
                Locations.name -> Locations
                Search.name -> Search
                Details.name -> Details
                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var showBottomBar by rememberSaveable { mutableStateOf(true) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            showBottomBar = when (navBackStackEntry?.destination?.route) {
                Screens.Home.name -> false
                else -> true
            }
            PanahonTheme {
                Scaffold(
                    bottomBar = { if (showBottomBar) PanahonBottomNavBar(navController = navController) }
                ) {
                    PanahonNavHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun PanahonNavHost(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = Screens.Home.name) {
        composable(Screens.Home.name) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(viewModel = viewModel, onLocationFound = {
                navController.navigate(
                    "${Screens.Details.name}/${it.locationName}/${it.latitude}/${it.longitude}"
                )
            }, onSnackbarButtonClick = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            })
        }
        composable(Screens.Locations.name) {
            val viewModel = hiltViewModel<LocationScreenViewModel>()
            LocationsScreen(viewModel = viewModel,
                title = stringResource(id = R.string.app_name),
                onSearchIconClicked = {
                    navController.navigate(Screens.Search.name)
                },
                onLocationClick = {
                    navController.navigate(
                        "${Screens.Details.name}/${it.name}/${it.latitude}/${it.longitude}"
                    )
                }
            )
        }
        composable(Screens.Search.name) {
            val viewModel = hiltViewModel<SearchLocationViewModel>()
            SearchScreen(
                viewModel = viewModel,
                onUpButtonClicked = {
                    navController.popBackStack()
                }
            )
        }
        composable("${Screens.Details.name}/{location}/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.StringType },
                navArgument("longitude") { type = NavType.StringType }
            )) {
            val viewModel = hiltViewModel<DetailsScreenViewModel>()
            DetailsScreen(
                viewModel = viewModel,
                location = it.arguments!!.getString("location")!!,
                latitude = it.arguments!!.getString("latitude")!!,
                longitude = it.arguments!!.getString("longitude")!!
            )
        }
    }
}

@Composable
fun PanahonBottomNavBar(navController: NavController) {
    BottomNavigation(contentColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val items = Screens.values().filter { it != Screens.Home }
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name
                    )
                },
                label = { Text(text = item.name) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = Screens.fromRoute(currentRoute).name == item.name,
                onClick = {
                    navController.navigate(item.name) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }

}
