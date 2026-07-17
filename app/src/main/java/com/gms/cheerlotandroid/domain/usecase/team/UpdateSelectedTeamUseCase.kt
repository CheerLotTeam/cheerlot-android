package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.TeamSelectionRepository

// 팀 선택 저장을 그대로 위임하는 얇은 래퍼입니다.
class UpdateSelectedTeamUseCase(
    private val teamSelectionRepository: TeamSelectionRepository
) {
    suspend operator fun invoke(teamId: TeamId) {
        teamSelectionRepository.setSelectedTeamId(teamId)
    }
}
