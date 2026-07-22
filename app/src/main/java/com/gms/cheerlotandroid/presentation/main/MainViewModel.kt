package com.gms.cheerlotandroid.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// 후에 미니플레이어 상태까지 같이 관찰
class MainViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = getSelectedTeamUseCase()
        .map { MainUiState(selectedTeamId = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState()
        )
}
