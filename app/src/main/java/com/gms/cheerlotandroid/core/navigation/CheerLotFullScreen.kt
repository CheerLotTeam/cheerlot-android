package com.gms.cheerlotandroid.core.navigation

import com.gms.cheerlotandroid.domain.model.team.TeamId

sealed interface CheerLotFullScreen {
    data class LineupPlayback(
        val startIndex: Int,
    ) : CheerLotFullScreen

    data class BasePlayback(
        val teamId: TeamId,
        val cheerSongId: String,
        val playerName: String,
        val source: PlaybackSource,
    ) : CheerLotFullScreen
}

enum class PlaybackSource {
    BASE,
    LINEUP
}
