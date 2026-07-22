package com.gms.cheerlotandroid.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
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
        viewModelScope.launch {
            updateSelectedTeamUseCase(teamId)
            onComplete()
        }
    }
}
