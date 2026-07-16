package com.gms.cheerlotandroid.data.storage.local.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.gms.cheerlotandroid.data.storage.local.entity.CheerSongEntity

// 응원가는 항상 선수와 같이 다뤄집니다 (PlayerWithCheerSongs로 조회, 선수 upsert할 때 같이 upsert).
// player_id FK가 ON DELETE CASCADE라 선수가 지워지면 응원가도 자동으로 같이 지워집니다.
@Dao
interface CheerSongDao {
    @Upsert
    suspend fun upsertAll(cheerSongs: List<CheerSongEntity>)
}
