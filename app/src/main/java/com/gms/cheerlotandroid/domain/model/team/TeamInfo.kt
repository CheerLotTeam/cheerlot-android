package com.gms.cheerlotandroid.domain.model.team

data class TeamInfo(
    val id: TeamId,
    val shortName: String,
    val longName: String,
    val englishFullName: String,
    val slogan: String
)
