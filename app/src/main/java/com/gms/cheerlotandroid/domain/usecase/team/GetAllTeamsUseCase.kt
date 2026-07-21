package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.TeamInfo
import com.gms.cheerlotandroid.domain.repository.TeamRepository

class GetAllTeamsUseCase(
    private val teamRepository: TeamRepository
) {
    operator fun invoke(): List<TeamInfo> {
        return teamRepository.getAllTeams()
    }
}
