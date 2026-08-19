package com.gms.cheerlotandroid.data.storage.local.entity

import androidx.room.Embedded
import androidx.room.Relation

// 연관 객체를 가져오는 것과 같은 편의 기능
// Entity가 아닌 조회 결과를 담는 그릇 역할
data class PlayerWithCheerSongs(
    @Embedded
    val player: PlayerEntity,
    @Relation(
        parentColumn = "player_id",
        entityColumn = "player_id"
    )
    val cheerSongs: List<CheerSongEntity>
)
