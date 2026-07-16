package com.gms.cheerlotandroid.domain.usecase.player

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.repository.PlayerRepository

// 캐시 전용 조회를 그대로 위임하는 얇은 래퍼입니다 (네트워크 호출 없음).
class GetPlayerDetailUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerId: String): PlayerInfo {
        return playerRepository.getPlayerDetail(playerId)
    }
}
