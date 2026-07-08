package com.gms.cheerlotandroid.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gms.cheerlotandroid.core.navigation.CheerLotMainTab
import com.gms.cheerlotandroid.core.navigation.CheerLotNavigator
import com.gms.cheerlotandroid.core.navigation.CheerLotRoute

private const val MAIN_ROUTE = "main"

@Composable
fun CheerLotNavHost(
    navigator: CheerLotNavigator,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var previousStackSize by remember { mutableIntStateOf(0) }

    BackHandler(enabled = navigator.routeStack.isNotEmpty()) {
        navigator.popBackStack()
    }

    LaunchedEffect(navigator.routeStack, currentRoute) {
        val targetRoute = navigator.routeStack.lastOrNull()?.route

        if (targetRoute == null) {
            if (currentRoute != MAIN_ROUTE) {
                navController.popBackStack(
                    route = MAIN_ROUTE,
                    inclusive = false,
                )
            }
            previousStackSize = navigator.routeStack.size
            return@LaunchedEffect
        }

        if (currentRoute != targetRoute) {
            val popped = if (navigator.routeStack.size < previousStackSize) {
                navController.popBackStack(
                    route = targetRoute,
                    inclusive = false,
                )
            } else {
                false
            }

            if (!popped) {
                navController.navigate(targetRoute) {
                    launchSingleTop = true
                }
            }
        }

        previousStackSize = navigator.routeStack.size
    }

    NavHost(
        navController = navController,
        startDestination = MAIN_ROUTE,
        modifier = modifier,
    ) {
        composable(MAIN_ROUTE) {
            MainPlaceholderScreen(selectedTab = navigator.selectedTab)
        }
        composable(CheerLotRoute.Settings.route) {
            PlaceholderScreen(title = "Settings")
        }
        composable(CheerLotRoute.ServiceInfo.route) {
            PlaceholderScreen(title = "Service Info")
        }
        composable(CheerLotRoute.MakerInfo.route) {
            PlaceholderScreen(title = "Maker Info")
        }
        composable(CheerLotRoute.TermsOfService.route) {
            PlaceholderScreen(title = "Terms Of Service")
        }
        composable(CheerLotRoute.PrivacyPolicy.route) {
            PlaceholderScreen(title = "Privacy Policy")
        }
        composable(CheerLotRoute.Copyright.route) {
            PlaceholderScreen(title = "Copyright")
        }
    }
}

@Composable
private fun MainPlaceholderScreen(
    selectedTab: CheerLotMainTab,
) {
    PlaceholderScreen(title = "Main / ${selectedTab.route}")
}

@Composable
private fun PlaceholderScreen(
    title: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title)
    }
}
