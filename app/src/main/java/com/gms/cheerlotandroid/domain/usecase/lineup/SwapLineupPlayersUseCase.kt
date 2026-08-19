package com.gms.cheerlotandroid.domain.usecase.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.repository.PlayerRepository

class SwapLineupPlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(
        lineupPlayer: PlayerInfo,
        benchPlayer: PlayerInfo
    ) {
        val battingOrder = requireNotNull(lineupPlayer.battingOrder) {
            "The lineup player must have a batting order."
        }
        require(benchPlayer.battingOrder == null) {
            "The bench player must not have a batting order."
        }
        require(lineupPlayer.teamId == benchPlayer.teamId) {
            "Both players must belong to the same team."
        }
        require(lineupPlayer.id != benchPlayer.id) {
            "The lineup player and bench player must be different."
        }

        playerRepository.swapLineupPlayer(
            teamId = lineupPlayer.teamId,
            lineupPlayerId = lineupPlayer.id.value,
            benchPlayerId = benchPlayer.id.value,
            battingOrder = battingOrder
        )
    }
}
