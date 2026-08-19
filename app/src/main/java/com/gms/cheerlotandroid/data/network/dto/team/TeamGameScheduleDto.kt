package com.gms.cheerlotandroid.data.network.dto.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamGameScheduleDto(
    val teamCode: String,
    val recentGames: List<TeamRecentGameDto>
)
