package com.gms.cheerlotandroid.data.network.dto.cheersong

import kotlinx.serialization.Serializable

@Serializable
data class CheerSongDto(
    val title: String,
    val lyrics: String,
    val audioUrl: String
)
