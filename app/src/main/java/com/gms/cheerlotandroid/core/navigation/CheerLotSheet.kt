package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.team.TeamId

sealed interface CheerLotSheet {
    data class CheerSongList(
        val playerId: String,
    ) : CheerLotSheet

    data class LineupChange(
        val playerId: String,
    ) : CheerLotSheet

    data class TeamChange(
        val selectedTeamId: TeamId,
    ) : CheerLotSheet

    data object Inquiry : CheerLotSheet

    data object ServicePage : CheerLotSheet
}
