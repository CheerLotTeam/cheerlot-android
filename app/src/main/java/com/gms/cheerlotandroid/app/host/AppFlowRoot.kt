package com.gms.cheerlotandroid.app.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotDialog
import com.gms.cheerlotandroid.presentation.appflow.AppFlowState
import com.gms.cheerlotandroid.presentation.appflow.AppFlowViewModel
import com.gms.cheerlotandroid.presentation.onboarding.TeamSelectMode
import com.gms.cheerlotandroid.presentation.onboarding.TeamSelectScreen
import com.gms.cheerlotandroid.presentation.appflow.SplashScreen

// 앱의 최상위 화면을 완전히 교체하는 root입니다
// Splash -> (팀 선택 여부에 따라) Onboarding 또는 Main으로 전환합니다.
@Composable
fun AppFlowRoot(
    modifier: Modifier = Modifier,
    presentationState: CheerLotPresentationState,
    onExitApp: () -> Unit,
    onOpenStore: () -> Unit,
) {
    val viewModel: AppFlowViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    // ViewModel의 진입 차단 상태를 공통 Dialog 상태로 한 번만 변환합니다.
    LaunchedEffect(state) {
        when (val currentState = state) {
            is AppFlowState.ServerChecking -> {
                presentationState.showDialog(
                    CheerLotDialog.Confirm(
                        title = "서비스 점검 안내",
                        message = currentState.message,
                        onConfirm = onExitApp,
                        dismissible = false,
                    )
                )
            }

            is AppFlowState.UpdateRequired -> {
                presentationState.showDialog(
                    CheerLotDialog.Confirm(
                        title = "최신 업데이트 안내",
                        message = "안정적인 서비스 사용을 위해\n최신 버전으로 업데이트해 주세요",
                        onConfirm = onOpenStore,
                        dismissible = false,
                    )
                )
            }

            else -> Unit
        }
    }

    when (state) {
        AppFlowState.Splash -> SplashScreen(
            onFinished = viewModel::onSplashFinished,
            modifier = modifier
        )

        AppFlowState.Onboarding -> TeamSelectScreen(
            mode = TeamSelectMode.ONBOARDING,
            onComplete = viewModel::onTeamSelected,
            modifier = modifier
        )

        AppFlowState.Main -> {
            CheerLotNavHost(presentationState = presentationState, modifier = modifier)
            CheerLotModalHost(presentationState = presentationState)
        }

        is AppFlowState.ServerChecking,
        is AppFlowState.UpdateRequired -> SplashScreen(onFinished = {}, modifier = modifier)
    }

    CheerLotDialogHost(presentationState = presentationState)
}
