package com.gms.cheerlotandroid.domain.usecase.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

// 라인업을 필요시, 동기화하고 그 결과를 관찰하는 Flow를 리턴합니다.
// syncLineup 내부에서 lineupVersion이 바뀐 경우에 한해 오늘 경기 정보도 같이 갱신됩니다.
class GetLineupUseCase(
    private val playerRepository: PlayerRepository,
    private val forceRefresh: Boolean = false
) {
    suspend operator fun invoke(teamId: TeamId): Flow<List<PlayerInfo>> {
        playerRepository.syncLineup(teamId, forceRefresh)
        return playerRepository.observeLineup(teamId)
    }
}
