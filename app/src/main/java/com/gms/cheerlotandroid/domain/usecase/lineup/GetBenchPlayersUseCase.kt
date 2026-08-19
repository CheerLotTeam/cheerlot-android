package com.gms.cheerlotandroid.domain.usecase.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class GetBenchPlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(teamId: TeamId): Flow<List<PlayerInfo>> {
        return playerRepository.observeBenchPlayers(teamId)
    }
}
