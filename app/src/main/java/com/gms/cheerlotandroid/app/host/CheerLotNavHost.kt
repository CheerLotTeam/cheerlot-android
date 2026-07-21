package com.gms.cheerlotandroid.app.host

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
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotNavigator
import com.gms.cheerlotandroid.core.navigation.CheerLotRoute
import com.gms.cheerlotandroid.core.navigation.PlaybackSource
import com.gms.cheerlotandroid.presentation.main.MainTabScreen

private const val MAIN_ROUTE = "main"

// 앱의 실제 화면 route를 Navigation Compose에 연결하는 root NavHost입니다.
@Composable
fun CheerLotNavHost(
    navigator: CheerLotNavigator,
    modifier: Modifier = Modifier,
) {
    // Navigation Compose의 실제 back stack은 이 NavController가 관리합니다.
    val navController = rememberNavController()

    // 현재 destination을 관찰해 Navigator의 routeStack과 동기화합니다.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // routeStack이 줄어든 경우 navigate가 아니라 popBackStack을 수행하기 위한 기준값입니다.
    var previousStackSize by remember { mutableIntStateOf(0) }

    // Android 시스템 뒤로가기도 Navigator를 통해 처리해 routeStack과 NavController가 어긋나지 않게 합니다.
    // modal(Dialog/FullScreen/Sheet)이 떠 있으면 routeStack보다 먼저 닫습니다
    val hasDialog = navigator.currentDialog != null
    val hasFullScreen = navigator.currentFullScreen != null
    val hasSheet = navigator.currentSheet != null
    BackHandler(
        enabled = hasDialog || hasFullScreen || hasSheet || navigator.routeStack.isNotEmpty(),
    ) {
        when {
            hasDialog -> navigator.dismissDialog()
            hasFullScreen -> navigator.dismissFullScreen()
            hasSheet -> navigator.dismissSheet()
            else -> navigator.popBackStack()
        }
    }

    // Navigator 상태가 바뀌면 실제 NavController 동작으로 반영합니다.
    LaunchedEffect(navigator.routeStack, currentRoute) {
        val targetRoute = navigator.routeStack.lastOrNull()?.route

        // routeStack이 비면 main route로 복귀합니다.
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

        // targetRoute가 바뀐 경우 push 또는 pop 동작을 실제 NavController에 적용합니다.
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

        // 다음 routeStack 변경에서 push/pop 방향을 판단하기 위해 현재 크기를 저장합니다.
        previousStackSize = navigator.routeStack.size
    }

    NavHost(
        navController = navController,
        startDestination = MAIN_ROUTE,
        modifier = modifier,
    ) {
        composable(MAIN_ROUTE) {
            MainTabScreen(
                selectedDestination = navigator.selectedTab,
                onDestinationSelected = navigator::selectTab,
                onOpenBasePlayback = { teamId, cheerSongId, playerName ->
                    navigator.showFullScreen(
                        CheerLotFullScreen.BasePlayback(
                            teamId = teamId,
                            cheerSongId = cheerSongId,
                            playerName = playerName,
                            source = PlaybackSource.TEAMMEMBERS
                        )
                    )
                },
                onOpenLineupPlayback = { startIndex ->
                    navigator.showFullScreen(
                        CheerLotFullScreen.LineupPlayback(startIndex = startIndex)
                    )
                }
            )
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

// 임시뷰
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
