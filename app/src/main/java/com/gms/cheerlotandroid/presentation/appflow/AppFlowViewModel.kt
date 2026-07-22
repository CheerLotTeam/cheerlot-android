package com.gms.cheerlotandroid.presentation.appflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.usecase.team.HasSelectedTeamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppFlowViewModel(
    private val hasSelectedTeamUseCase: HasSelectedTeamUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AppFlowState>(AppFlowState.Splash)
    val state: StateFlow<AppFlowState> = _state.asStateFlow()

    // 스플래시 애니메이션이 끝난 시점에 팀 선택 여부를 확인해 다음 화면을 정합니다.
    fun onSplashFinished() {
        viewModelScope.launch {
            val hasSelectedTeam = hasSelectedTeamUseCase().first()
            _state.value = if (hasSelectedTeam) AppFlowState.Main else AppFlowState.Onboarding
        }
    }

    fun onTeamSelected() {
        _state.value = AppFlowState.Main
    }
}
