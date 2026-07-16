package com.gms.cheerlotandroid.domain.model.player

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId

data class PlayerInfo(
    val id: PlayerId,
    val teamId: TeamId,
    val name: String,
    val backNumber: Int,
    val position: String,
    val batThrow: String,
    val battingOrder: Int?,
    val cheerSongs: List<CheerSongInfo>
)
