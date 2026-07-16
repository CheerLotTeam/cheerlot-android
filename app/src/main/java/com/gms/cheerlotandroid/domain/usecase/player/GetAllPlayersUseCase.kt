package com.gms.cheerlotandroid.domain.usecase.player

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

// 전체 로스터를 동기화(필요시)하고 그 결과를 관찰하는 Flow를 리턴합니다.
class GetAllPlayersUseCase(
    private val playerRepository: PlayerRepository,
    private val forceRefresh: Boolean = false
) {
    suspend operator fun invoke(teamId: TeamId): Flow<List<PlayerInfo>> {
        playerRepository.syncAllPlayers(teamId, forceRefresh)
        return playerRepository.observeAllPlayers(teamId)
    }
}
