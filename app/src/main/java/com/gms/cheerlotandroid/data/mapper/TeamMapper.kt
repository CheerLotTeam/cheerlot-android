package com.gms.cheerlotandroid.data.mapper

import com.gms.cheerlotandroid.data.network.dto.team.TeamGameDto
import com.gms.cheerlotandroid.data.network.dto.team.TeamGameScheduleDto
import com.gms.cheerlotandroid.data.network.dto.team.TeamRecentGameDto
import com.gms.cheerlotandroid.data.network.dto.team.TeamVersionsDto
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.data.storage.local.entity.TeamEntity
import com.gms.cheerlotandroid.data.storage.local.entity.TeamRecentGameLocalDto
import com.gms.cheerlotandroid.domain.model.team.GameScheduleInfo
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamVersionInfo

internal fun TeamVersionsDto.toDomain(teamId: TeamId): TeamVersionInfo {
    return TeamVersionInfo(
        teamId = teamId,
        lineupVersion = lineupVersion,
        playersVersion = playersVersion
    )
}

internal fun TeamEntity.toVersionInfo(): TeamVersionInfo {
    return TeamVersionInfo(
        teamId = TeamId(teamId),
        lineupVersion = lineupVersion,
        playersVersion = playersVersion
    )
}

// isSeasonEnded/hasTodayGame/lineupUpdatedToday는 전부 서버가 주는 원본 사실이라,
// 이걸 조합한 LINEUP_PENDING도 클라이언트가 지어낸 상태가 아니라 서버 사실의 재조합입니다.
private fun resolveGameStatus(
    isSeasonEnded: Boolean,
    hasTodayGame: Boolean,
    lineupUpdatedToday: Boolean
): GameStatus {
    return when {
        isSeasonEnded -> GameStatus.SEASON_ENDED
        hasTodayGame && lineupUpdatedToday -> GameStatus.PLAYING_TODAY
        hasTodayGame -> GameStatus.LINEUP_PENDING
        else -> GameStatus.OFF_DAY
    }
}

internal fun TeamGameDto.toDomain(teamId: TeamId): TeamGameInfo {
    return TeamGameInfo(
        teamId = teamId,
        status = resolveGameStatus(isSeasonEnded, hasTodayGame, lineupUpdatedToday),
        opponentTeamId = opponentTeamCode?.let { TeamCatalog.findByApiCode(it)?.id },
        starterPitcherName = starterPitcherName,
        lastGameDate = lastGameDate
    )
}

internal fun TeamEntity.toGameInfo(): TeamGameInfo {
    return TeamGameInfo(
        teamId = TeamId(teamId),
        status = resolveGameStatus(isSeasonEnded, hasTodayGame, lineupUpdatedToday),
        opponentTeamId = opponentTeamId?.let { TeamId(it) },
        starterPitcherName = starterPitcherName,
        lastGameDate = lastGameDate,
        isHome = isHome
    )
}

internal fun TeamRecentGameDto.toLocalDto(): TeamRecentGameLocalDto {
    return TeamRecentGameLocalDto(
        date = date,
        hasGame = hasGame,
        opponentTeamId = opponentTeamCode?.let { TeamCatalog.findByApiCode(it)?.id?.value },
        isHome = isHome,
        starterPitcherName = starterPitcherName,
        opponentStarterPitcherName = opponentStarterPitcherName
    )
}

internal fun TeamRecentGameLocalDto.toDomain(): GameScheduleInfo {
    return GameScheduleInfo(
        date = date,
        hasGame = hasGame,
        opponentTeamId = opponentTeamId?.let { TeamId(it) },
        isHome = isHome,
        starterPitcherName = starterPitcherName,
        opponentStarterPitcherName = opponentStarterPitcherName
    )
}

internal fun TeamGameScheduleDto.toLocalDtoList(): List<TeamRecentGameLocalDto> {
    return recentGames.map { it.toLocalDto() }
}

internal fun List<TeamRecentGameLocalDto>.toDomainList(): List<GameScheduleInfo> {
    return map { it.toDomain() }
}
