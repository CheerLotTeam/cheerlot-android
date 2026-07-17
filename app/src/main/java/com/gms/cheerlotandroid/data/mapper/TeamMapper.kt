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

internal fun TeamGameDto.toDomain(teamId: TeamId): TeamGameInfo {
    val status = when {
        isSeasonEnded -> GameStatus.SEASON_ENDED
        hasTodayGame -> GameStatus.PLAYING_TODAY
        else -> GameStatus.OFF_DAY
    }
    return TeamGameInfo(
        teamId = teamId,
        status = status,
        opponentTeamId = opponentTeamCode?.let { TeamCatalog.findByApiCode(it)?.id },
        starterPitcherName = starterPitcherName,
        lastGameDate = lastGameDate,
        lineupUpdatedToday = lineupUpdatedToday
    )
}

internal fun TeamEntity.toGameInfo(): TeamGameInfo {
    val status = when {
        isSeasonEnded -> GameStatus.SEASON_ENDED
        hasTodayGame -> GameStatus.PLAYING_TODAY
        else -> GameStatus.OFF_DAY
    }
    return TeamGameInfo(
        teamId = TeamId(teamId),
        status = status,
        opponentTeamId = opponentTeamId?.let { TeamId(it) },
        starterPitcherName = starterPitcherName,
        lastGameDate = lastGameDate,
        lineupUpdatedToday = lineupUpdatedToday,
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
