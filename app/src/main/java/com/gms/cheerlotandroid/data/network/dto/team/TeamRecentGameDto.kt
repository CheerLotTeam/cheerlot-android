package com.gms.cheerlotandroid.data.network.dto.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamRecentGameDto(
    val date: String,
    val hasGame: Boolean,
    val opponentTeamCode: String? = null,
    val isHome: Boolean? = null,
    val starterPitcherName: String? = null,
    val opponentStarterPitcherName: String? = null
)
