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
        // 앱 진입/팀 변경/아이콘 모드 변경마다 현재 상태에 맞는 런처 아이콘으로 동기화합니다.
        // 백업 복원 등으로 DataStore와 PackageManager의 컴포넌트 활성 상태가 어긋날 수 있어,
        // Main 화면을 구독할 때마다(재구독 포함) 매번 다시 맞춰줍니다.
        .onEach { (selectedTeamId, appIconMode) -> appIconSwitcher.switchTo(selectedTeamId, appIconMode) }
        .map { (selectedTeamId, _) -> MainUiState(selectedTeamId = selectedTeamId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState()
        )
}
