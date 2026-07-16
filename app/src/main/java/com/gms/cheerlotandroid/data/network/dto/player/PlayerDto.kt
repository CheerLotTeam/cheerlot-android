package com.gms.cheerlotandroid.data.network.dto.player

import com.gms.cheerlotandroid.data.network.dto.cheersong.CheerSongDto
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val playerCode: String,
    val name: String,
    val teamCode: String,
    val position: String? = null,
    val batThrow: String? = null,
    val backNumber: Int,
    val battingOrder: Int? = null,
    val cheerSongs: List<CheerSongDto> = emptyList()
)
