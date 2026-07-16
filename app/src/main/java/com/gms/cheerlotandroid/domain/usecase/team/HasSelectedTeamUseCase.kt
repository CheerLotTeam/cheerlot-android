package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 팀 선택 여부만 파생해서 노출합니다.
class HasSelectedTeamUseCase(
    private val teamSelectionRepository: TeamSelectionRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return teamSelectionRepository.selectedTeamId.map { it != null }
    }
}
