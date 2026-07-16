package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.Flow

// 선택된 팀 Flow를 그대로 위임하는 얇은 래퍼입니다.
class GetSelectedTeamUseCase(
    private val teamSelectionRepository: TeamSelectionRepository
) {
    operator fun invoke(): Flow<TeamId?> {
        return teamSelectionRepository.selectedTeamId
    }
}
