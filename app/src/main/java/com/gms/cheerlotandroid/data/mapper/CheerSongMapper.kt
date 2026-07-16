package com.gms.cheerlotandroid.data.mapper

import com.gms.cheerlotandroid.data.network.dto.cheersong.CheerSongDto
import com.gms.cheerlotandroid.data.storage.local.entity.CheerSongEntity
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo

internal fun CheerSongDto.toEntity(playerId: String): CheerSongEntity {
    return CheerSongEntity.create(
        playerId = playerId,
        title = title,
        lyrics = lyrics,
        audioUrl = audioUrl
    )
}

internal fun CheerSongEntity.toDomain(): CheerSongInfo {
    return CheerSongInfo(
        id = cheerSongId,
        title = title,
        lyrics = lyrics,
        audioUrl = audioUrl
    )
}
