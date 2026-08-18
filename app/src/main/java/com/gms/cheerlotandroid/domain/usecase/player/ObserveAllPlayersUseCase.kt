package com.gms.cheerlotandroid.domain.usecase.player

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

// 동기화 없이 로컬 로스터만 관찰합니다(iOS TeamPlayersSyncUseCase.getAllPlayers 대응).
// 동기화가 필요하면 GetAllPlayersUseCase(sync + observe)를 씁니다.
class ObserveAllPlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(teamId: TeamId): Flow<List<PlayerInfo>> =
        playerRepository.observeAllPlayers(teamId)
}
