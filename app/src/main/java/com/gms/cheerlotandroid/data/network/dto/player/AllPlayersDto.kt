package com.gms.cheerlotandroid.data.network.dto.player

import kotlinx.serialization.Serializable

@Serializable
data class AllPlayersDto(
    val teamCode: String,
    val players: List<PlayerDto>
)
