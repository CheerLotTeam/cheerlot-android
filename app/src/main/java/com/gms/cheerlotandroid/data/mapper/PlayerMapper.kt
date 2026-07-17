package com.gms.cheerlotandroid.data.mapper

import com.gms.cheerlotandroid.data.network.dto.player.PlayerDto
import com.gms.cheerlotandroid.data.network.dto.player.StarterDto
import com.gms.cheerlotandroid.data.storage.local.entity.PlayerEntity
import com.gms.cheerlotandroid.data.storage.local.entity.PlayerWithCheerSongs
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId

private const val DEFAULT_POSITION = "교체선수"
private const val DEFAULT_BAT_THROW = ""

internal fun StarterDto.toPlayerEntity(teamId: TeamId): PlayerEntity {
    return PlayerEntity(
        playerId = playerCode,
        teamId = teamId.value,
        name = name,
        backNumber = backNumber,
        position = position ?: DEFAULT_POSITION,
        batThrow = batThrow ?: DEFAULT_BAT_THROW,
        battingOrder = battingOrder
    )
}

internal fun PlayerDto.toPlayerEntity(): PlayerEntity {
    return PlayerEntity(
        playerId = playerCode,
        teamId = teamCode,
        name = name,
        backNumber = backNumber,
        position = position ?: DEFAULT_POSITION,
        batThrow = batThrow ?: DEFAULT_BAT_THROW,
        battingOrder = battingOrder
    )
}

internal fun PlayerWithCheerSongs.toDomain(): PlayerInfo {
    return PlayerInfo(
        id = PlayerId(player.playerId),
        teamId = TeamId(player.teamId),
        name = player.name,
        backNumber = player.backNumber,
        position = player.position,
        batThrow = player.batThrow,
        battingOrder = player.battingOrder,
        cheerSongs = cheerSongs.map { it.toDomain() }
    )
}
