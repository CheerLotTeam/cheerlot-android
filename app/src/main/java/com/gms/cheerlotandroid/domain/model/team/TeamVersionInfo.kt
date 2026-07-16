package com.gms.cheerlotandroid.domain.model.team

data class TeamVersionInfo(
    val teamId: TeamId,
    val lineupVersion: Int,
    val playersVersion: Int
)
