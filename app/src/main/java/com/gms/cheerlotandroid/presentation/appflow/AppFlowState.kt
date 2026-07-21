package com.gms.cheerlotandroid.presentation.appflow

// 앱 실행 시 최상위 화면 자체를 교체하는 상태입니다.
sealed interface AppFlowState {
    data object Splash : AppFlowState
    data object Onboarding : AppFlowState
    data object Main : AppFlowState
}
