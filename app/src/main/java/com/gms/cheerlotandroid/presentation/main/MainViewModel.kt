package com.gms.cheerlotandroid.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.core.icon.AppIconSwitcher
import com.gms.cheerlotandroid.domain.usecase.settings.GetAppIconModeUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

// 후에 미니플레이어 상태까지 같이 관찰
class MainViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase,
    getAppIconModeUseCase: GetAppIconModeUseCase,
    appIconSwitcher: AppIconSwitcher
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = combine(
        getSelectedTeamUseCase(),
        getAppIconModeUseCase()
    ) { selectedTeamId, appIconMode -> selectedTeamId to appIconMode }
        // 앱 진입/팀 변경/아이콘 모드 변경마다 원하는 런처 아이콘 상태를 기록해둡니다.
        // 실제 PackageManager 반영은 MainActivity.onPause()에서 한 번에 처리합니다
        // (즉시 반영하면 시스템이 앱을 포그라운드에서 내려버리는 부작용이 있어서).
        .onEach { (selectedTeamId, appIconMode) -> appIconSwitcher.requestSwitch(selectedTeamId, appIconMode) }
        .map { (selectedTeamId, _) -> MainUiState(selectedTeamId = selectedTeamId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState()
        )
}
