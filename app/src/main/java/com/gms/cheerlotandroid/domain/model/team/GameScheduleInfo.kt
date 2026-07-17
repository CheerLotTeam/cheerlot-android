package com.gms.cheerlotandroid.domain.model.team

data class GameScheduleInfo(
    val date: String,
    val hasGame: Boolean,
    val opponentTeamId: TeamId?,
    val isHome: Boolean?,
    val starterPitcherName: String?,
    val opponentStarterPitcherName: String?
)
