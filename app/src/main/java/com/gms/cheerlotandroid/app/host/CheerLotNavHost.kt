package com.gms.cheerlotandroid.app.host

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotRoute
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet
import com.gms.cheerlotandroid.core.navigation.PlaybackSource
import com.gms.cheerlotandroid.presentation.main.MainScreen
import com.gms.cheerlotandroid.presentation.settings.AppLinks
import com.gms.cheerlotandroid.presentation.settings.LegalContent
import com.gms.cheerlotandroid.presentation.settings.MakerInfoScreen
import com.gms.cheerlotandroid.presentation.settings.ServiceAppInfoScreen
import com.gms.cheerlotandroid.presentation.settings.ServiceInfoScreen
import com.gms.cheerlotandroid.presentation.settings.SettingsScreen
import com.gms.cheerlotandroid.presentation.settings.SettingsViewModel

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
                        CheerLotFullScreen.LineupPlayback(
                            startIndex = startIndex
                        )
                    )
                },
                onOpenCheerSongMenu = { action ->
                    presentationState.showSheet(
                        CheerLotSheet.CheerSongList(
                            member = action.member,
                            startIndex = action.startIndex,
                            queueSongs = action.queueSongs,
                            queuePlayerNames = action.queuePlayerNames,
                            queuePlayerIds = action.queuePlayerIds,
                            isGameDay = action.isGameDay,
                        )
                    )
                },
                onOpenLineupChange = { member ->
                    presentationState.showSheet(
                        CheerLotSheet.LineupChange(
                            member = member
                        )
                    )
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
            val context = LocalContext.current
            val viewModel: SettingsViewModel =
                viewModel(factory = LocalAppContainer.current.viewModelFactory)
            val uiState by viewModel.uiState.collectAsState()

            SettingsScreen(
                state = uiState,
                onTapTeamCard = {
                    presentationState.showSheet(CheerLotSheet.TeamChange)
                },
                onSelectAppIconMode = viewModel::onSelectAppIconMode,
                onTapServiceInfo = {
                    navController.navigate(CheerLotRoute.ServiceInfo.route)
                },
                onTapMakerInfo = {
                    navController.navigate(CheerLotRoute.MakerInfo.route)
                },
                // "대표 페이지"(onTapMainPage)와 동일하게, 문의하기도 앱 내 시트가 아니라
                // 외부 브라우저로 구글폼을 엽니다.
                onTapInquiry = {
                    context.openExternalUrl(AppLinks.INQUIRY_FORM_URL)
                },
                onDismissToast = viewModel::dismissToast,
                onBack = navController::navigateUp
            )
        }
        composable(CheerLotRoute.ServiceInfo.route) {
            val context = LocalContext.current

            ServiceInfoScreen(
                onTapMainPage = { context.openExternalUrl(AppLinks.MAIN_PAGE_URL) },
                onTapTerms = { navController.navigate(CheerLotRoute.TermsOfService.route) },
                onTapPrivacy = { navController.navigate(CheerLotRoute.PrivacyPolicy.route) },
                onTapCopyright = { navController.navigate(CheerLotRoute.Copyright.route) },
                onBack = navController::navigateUp
            )
        }
        composable(CheerLotRoute.MakerInfo.route) {
            val context = LocalContext.current

            MakerInfoScreen(
                onTapInstagram = { context.openExternalUrl(AppLinks.INSTAGRAM_URL) },
                onTapStoreReview = { context.openExternalUrl(AppLinks.PLAY_STORE_URL) },
                onBack = navController::navigateUp
            )
        }
        composable(CheerLotRoute.TermsOfService.route) {
            ServiceAppInfoScreen(
                title = "이용약관",
                body = LegalContent.termsOfService,
                onBack = navController::navigateUp
            )
        }
        composable(CheerLotRoute.PrivacyPolicy.route) {
            ServiceAppInfoScreen(
                title = "개인정보처리방침",
                body = LegalContent.privacyPolicy,
                onBack = navController::navigateUp
            )
        }
        composable(CheerLotRoute.Copyright.route) {
            ServiceAppInfoScreen(
                title = "저작권 법적고지",
                body = LegalContent.copyrightPolicy,
                onBack = navController::navigateUp
            )
        }
    }
}

private fun Context.openExternalUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}
