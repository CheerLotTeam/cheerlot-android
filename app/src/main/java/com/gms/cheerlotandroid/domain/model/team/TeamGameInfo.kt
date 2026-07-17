package com.gms.cheerlotandroid.domain.model.team

data class TeamGameInfo(
    val teamId: TeamId,
    val status: GameStatus,
    val opponentTeamId: TeamId?,
    val starterPitcherName: String?,
    val lastGameDate: String?,
    val lineupUpdatedToday: Boolean,
    // 서버 오늘 경기 정보 응답(TeamGameDto)에는 이 값이 없어 null입니다.
    val isHome: Boolean? = null
)
