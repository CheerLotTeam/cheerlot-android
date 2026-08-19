package com.gms.cheerlotandroid.data.network.dto.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamGameDto(
    val teamCode: String,
    val isSeasonEnded: Boolean,
    val lastGameDate: String,
    val hasTodayGame: Boolean,
    val opponentTeamCode: String? = null,
    val starterPitcherName: String? = null,
    val lineupUpdatedToday: Boolean
)
