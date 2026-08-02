package com.gms.cheerlotandroid.app.host

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotRoute
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet
import com.gms.cheerlotandroid.core.navigation.PlaybackSource
import com.gms.cheerlotandroid.design.component.CustomTopAppBarBackWithTitle
import com.gms.cheerlotandroid.presentation.main.MainScreen

private const val MAIN_ROUTE = "main"

// 앱의 실제 화면 route를 Navigation Compose에 연결하는 root NavHost입니다.
@Composable
fun CheerLotNavHost(
    presentationState: CheerLotPresentationState,
    modifier: Modifier = Modifier,
) {
    // Navigation Compose의 실제 back stack은 이 NavController가 관리합니다.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MAIN_ROUTE,
        modifier = modifier,
    ) {
        composable(MAIN_ROUTE) {
            MainScreen(
                onOpenBasePlayback = { teamId, cheerSongId, playerName ->
                    presentationState.showFullScreen(
                        CheerLotFullScreen.BasePlayback(
                            teamId = teamId,
                            cheerSongId = cheerSongId,
                            playerName = playerName,
                            source = PlaybackSource.TEAMMEMBERS
                        )
                    )
                },
                onOpenLineupPlayback = { startIndex ->
                    presentationState.showFullScreen(
                        CheerLotFullScreen.LineupPlayback(startIndex = startIndex)
                    )
                },
                onOpenLineupChange = { playerId ->
                    presentationState.showSheet(CheerLotSheet.LineupChange(playerId = playerId))
                },
                onShowDialog = presentationState::showDialog,
                onOpenSettings = {
                    navController.navigate(CheerLotRoute.Settings.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(CheerLotRoute.Settings.route) {
            PlaceholderScreen(title = "설정", onNavigateUp = navController::navigateUp)
        }
        composable(CheerLotRoute.ServiceInfo.route) {
            PlaceholderScreen(title = "서비스 정보", onNavigateUp = navController::navigateUp)
        }
        composable(CheerLotRoute.MakerInfo.route) {
            PlaceholderScreen(title = "만든 사람", onNavigateUp = navController::navigateUp)
        }
        composable(CheerLotRoute.TermsOfService.route) {
            PlaceholderScreen(title = "이용약관", onNavigateUp = navController::navigateUp)
        }
        composable(CheerLotRoute.PrivacyPolicy.route) {
            PlaceholderScreen(title = "개인정보 처리방침", onNavigateUp = navController::navigateUp)
        }
        composable(CheerLotRoute.Copyright.route) {
            PlaceholderScreen(title = "저작권", onNavigateUp = navController::navigateUp)
        }
    }

}

// 임시뷰
@Composable
private fun PlaceholderScreen(
    title: String,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            CustomTopAppBarBackWithTitle(title = title, onBack = onNavigateUp)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = title)
        }
    }
}
