package com.gms.cheerlotandroid.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.usecase.team.GetAllTeamsUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.UpdateSelectedTeamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamSelectViewModel(
    getAllTeamsUseCase: GetAllTeamsUseCase,
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val updateSelectedTeamUseCase: UpdateSelectedTeamUseCase,
    private val audioPlayer: AudioPlayer,
    val mode: TeamSelectMode = TeamSelectMode.ONBOARDING
) : ViewModel() {
    private val _uiState = MutableStateFlow(TeamSelectUiState(teams = getAllTeamsUseCase()))
    val uiState: StateFlow<TeamSelectUiState> = _uiState.asStateFlow()

    init {
        // onboarding이면 아직 선택된 팀이 없어 null, change면 현재 선택된 팀이 나옴
        viewModelScope.launch {
            val currentTeamId = getSelectedTeamUseCase().first()
            _uiState.update { it.copy(selectedTeamId = currentTeamId) }
        }
    }

    fun select(teamId: TeamId) {
        _uiState.update { it.copy(selectedTeamId = teamId) }
    }

    fun complete(onComplete: () -> Unit) {
        val state = _uiState.value
        val teamId = state.selectedTeamId ?: return
        if (state.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        // 팀이 바뀌면 이전 팀 응원가가 백그라운드/미니플레이어에 남아있지 않도록 정지합니다.
        // iOS TeamSelectViewModel.complete()와 동일하게 onboarding/change 구분 없이 항상 호출합니다.
        audioPlayer.stop()
        viewModelScope.launch {
            updateSelectedTeamUseCase(teamId)
            onComplete()
            // 이 ViewModel은 CHANGE 모드에서 시트를 열 때마다 새로 만들어지지 않고 재사용되므로,
            // 여기서 풀어주지 않으면 두 번째 팀 변경부터 isSubmitting=true에 막혀 완료가 계속 무시됩니다.
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }
}
