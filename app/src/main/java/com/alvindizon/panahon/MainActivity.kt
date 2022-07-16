package com.alvindizon.panahon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.details.ui.DetailsScreen
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel
import com.alvindizon.panahon.home.ui.HomeScreen
import com.alvindizon.panahon.home.viewmodel.HomeScreenViewModel
import com.alvindizon.panahon.locations.ui.LocationsScreen
import com.alvindizon.panahon.locations.viewmodel.LocationScreenViewModel
import com.alvindizon.panahon.searchlocation.search.SearchScreen
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screens {
    Locations,
    Search,
    Details,
    Home;
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
            val scope = rememberCoroutineScope()
            PanahonTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = { PanahonDrawer(scaffoldState, navController, scope) }
                ) {
                    PanahonNavHost(navController, scaffoldState, scope)
                }
            }
        }
    }
}

@Composable
fun PanahonNavHost(navController: NavHostController, scaffoldState: ScaffoldState, scope: CoroutineScope) {
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
        composable("${Screens.Details.name}/{location}/{latitude}/{longitude}", arguments = listOf(
            navArgument("location") { type = NavType.StringType },
            navArgument("latitude") { type = NavType.StringType },
            navArgument("longitude") { type = NavType.StringType }
        )) {
            val viewModel = hiltViewModel<DetailsScreenViewModel>()
            DetailsScreen(
                scaffoldState = scaffoldState,
                scope = scope,
                viewModel = viewModel,
                location = it.arguments!!.getString("location")!!,
                latitude = it.arguments!!.getString("latitude")!!,
                longitude = it.arguments!!.getString("longitude")!!
            )
        }
    }
}

@Composable
fun PanahonDrawer(
    scaffoldState: ScaffoldState,
    navController: NavController,
    scope: CoroutineScope
) {
    val items = Screens.values().filter { it != Screens.Details && it != Screens.Home }
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.open_menu)
        )
        for (item in items) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.clickable {
                    navController.navigate(item.name) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    // Close drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                })
        }
    }
}
