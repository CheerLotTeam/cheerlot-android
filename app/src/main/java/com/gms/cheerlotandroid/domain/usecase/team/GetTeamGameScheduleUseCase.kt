package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.GameScheduleInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow

// 앞으로 3개의 경기 스케줄을 필요시, 동기화하고 그 결과를 관찰하는 Flow를 리턴합니다.
class GetTeamGameScheduleUseCase(
    private val teamRepository: TeamRepository,
    private val forceRefresh: Boolean = false
) {
    suspend operator fun invoke(teamId: TeamId): Flow<List<GameScheduleInfo>> {
        teamRepository.syncGameSchedule(teamId, forceRefresh)
        return teamRepository.observeGameSchedule(teamId)
    }
}
