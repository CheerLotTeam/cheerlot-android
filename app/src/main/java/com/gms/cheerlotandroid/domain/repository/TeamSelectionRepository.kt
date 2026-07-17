package com.gms.cheerlotandroid.domain.repository

import com.gms.cheerlotandroid.domain.model.team.TeamId
import kotlinx.coroutines.flow.Flow

interface TeamSelectionRepository {
    // null이면 아직 팀을 선택한 적 없음.
    val selectedTeamId: Flow<TeamId?>

    suspend fun setSelectedTeamId(teamId: TeamId)
}
