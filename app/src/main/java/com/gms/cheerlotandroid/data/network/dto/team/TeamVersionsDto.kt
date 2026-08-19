package com.gms.cheerlotandroid.data.network.dto.team

import kotlinx.serialization.Serializable

@Serializable
data class TeamVersionsDto(
    val teamCode: String,
    val playersVersion: Int,
    val lineupVersion: Int
)
