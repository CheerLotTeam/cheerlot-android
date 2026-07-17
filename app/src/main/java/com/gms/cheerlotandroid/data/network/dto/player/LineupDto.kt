package com.gms.cheerlotandroid.data.network.dto.player

import kotlinx.serialization.Serializable

@Serializable
data class LineupDto(
    val teamCode: String,
    val role: String,
    val players: List<StarterDto>
)
