package com.gms.cheerlotandroid.domain.usecase.player

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository

// 로스터 동기화만 수행합니다(iOS TeamPlayersSyncUseCase.syncIfNeeded 대응).
// 예외는 그대로 던져, 호출부가 삼키고 캐시를 계속 보여줄 수 있게 합니다.
class SyncAllPlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(teamId: TeamId, forceRefresh: Boolean = false) {
        playerRepository.syncAllPlayers(teamId, forceRefresh)
    }
}
