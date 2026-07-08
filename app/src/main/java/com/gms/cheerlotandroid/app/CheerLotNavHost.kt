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

// 앱의 실제 화면 route를 Navigation Compose에 연결하는 root NavHost입니다.
//
// 새 push 화면을 추가할 때의 순서:
// 1. core/navigation/CheerLotRoute.kt에 route 타입을 추가합니다.
// 2. 아래 NavHost builder에 composable(route.route) 블록을 추가합니다.
// 3. 화면 이동이 필요한 곳에서는 navController를 직접 쓰지 않고 navigator.navigate(route)를 호출합니다.
// 4. argument가 있는 화면은 route pattern과 createRoute 규칙을 route 타입에 함께 정의합니다.
//
// Sheet, Dialog, FullScreen은 여기서 바로 처리하지 않고 navigator 상태로 분리해 둡니다.
// 실제 modal UI 연결은 팀 변경, 재생 화면 등 구체 화면이 생길 때 root ModalHost로 추가합니다.
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
    BackHandler(enabled = navigator.routeStack.isNotEmpty()) {
        navigator.popBackStack()
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
