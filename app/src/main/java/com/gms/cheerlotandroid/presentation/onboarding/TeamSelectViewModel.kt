package com.gms.cheerlotandroid.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.team.GetAllTeamsUseCase
import com.gms.cheerlotandroid.domain.usecase.team.UpdateSelectedTeamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamSelectViewModel(
    getAllTeamsUseCase: GetAllTeamsUseCase,
    private val updateSelectedTeamUseCase: UpdateSelectedTeamUseCase,
    val mode: TeamSelectMode = TeamSelectMode.ONBOARDING,
    initialSelectedTeamId: TeamId? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TeamSelectUiState(
            teams = getAllTeamsUseCase(),
            selectedTeamId = initialSelectedTeamId
        )
    )
    val uiState: StateFlow<TeamSelectUiState> = _uiState.asStateFlow()

    fun select(teamId: TeamId) {
        _uiState.update { it.copy(selectedTeamId = teamId) }
    }

    fun complete(onComplete: () -> Unit) {
        val teamId = _uiState.value.selectedTeamId ?: return
        viewModelScope.launch {
            updateSelectedTeamUseCase(teamId)
            onComplete()
        }
    }
}
