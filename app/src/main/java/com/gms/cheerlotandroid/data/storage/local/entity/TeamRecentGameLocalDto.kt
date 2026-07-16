package com.gms.cheerlotandroid.data.storage.local.entity

import kotlinx.serialization.Serializable

// TeamEntity.recentGamesJson 컬럼에 그대로 직렬화되어 저장되는 로컬 캐시 전용 DTO입니다.
// 네트워크 DTO(TeamRecentGameDto)와 분리해서, opponentTeamId는 API 코드가 아니라 내부 TeamId 값을 저장합니다.
@Serializable
data class TeamRecentGameLocalDto(
    val date: String,
    val hasGame: Boolean,
    val opponentTeamId: String? = null,
    val isHome: Boolean? = null,
    val starterPitcherName: String? = null,
    val opponentStarterPitcherName: String? = null
)
