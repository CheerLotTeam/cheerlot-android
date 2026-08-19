package com.gms.cheerlotandroid.presentation.onboarding

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

data class TeamSelectUiState(
    val teams: List<TeamInfo> = emptyList(),
    val selectedTeamId: TeamId? = null,
    val isSubmitting: Boolean = false
) {
    val isCompleteEnabled: Boolean
        get() = selectedTeamId != null && !isSubmitting
}
