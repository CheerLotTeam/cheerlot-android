package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo
import com.gms.cheerlotandroid.domain.repository.TeamRepository

class GetTeamUseCase(
    private val teamRepository: TeamRepository
) {
    operator fun invoke(teamId: TeamId): TeamInfo? {
        return teamRepository.getTeam(teamId)
    }
}
