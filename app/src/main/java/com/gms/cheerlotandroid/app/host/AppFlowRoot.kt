package com.gms.cheerlotandroid.app.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
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
) {
    val viewModel: AppFlowViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            CheerLotDialogHost(presentationState = presentationState)
            CheerLotModalHost(presentationState = presentationState)
        }
    }
}
