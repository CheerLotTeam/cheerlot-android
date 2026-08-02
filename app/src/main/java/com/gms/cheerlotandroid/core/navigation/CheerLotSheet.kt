package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId

// Bottom Sheet로 표시할 modal 흐름입니다.
// sheet와 full screen은 동시에 표시하지 않도록 Navigator에서 서로를 정리합니다.
sealed interface CheerLotSheet {
    data class CheerSongList(
        val member: PlayerInfo,
        val startIndex: Int,
    ) : CheerLotSheet

    data class LineupChange(
        val member: PlayerInfo,
    ) : CheerLotSheet

    data class TeamChange(
        val selectedTeamId: TeamId,
    ) : CheerLotSheet

    data object Inquiry : CheerLotSheet

    data object ServicePage : CheerLotSheet
}
