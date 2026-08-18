package com.gms.cheerlotandroid.domain.usecase.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

// 동기화 없이 로컬 라인업만 관찰합니다. 동기화가 필요하면 GetLineupUseCase를 씁니다.
class ObserveLineupUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(teamId: TeamId): Flow<List<PlayerInfo>> =
        playerRepository.observeLineup(teamId)
}
