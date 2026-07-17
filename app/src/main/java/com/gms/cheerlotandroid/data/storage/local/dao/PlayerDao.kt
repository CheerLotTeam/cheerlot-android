package com.gms.cheerlotandroid.data.storage.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.gms.cheerlotandroid.data.storage.local.entity.PlayerEntity
import com.gms.cheerlotandroid.data.storage.local.entity.PlayerWithCheerSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    // batting_order가 있는 선수만, 타순대로 이미 정렬돼서 나옵니다.
    @Transaction
    @Query("SELECT * FROM players WHERE team_id = :teamId AND batting_order IS NOT NULL ORDER BY batting_order ASC")
    fun observeLineupWithCheerSongs(teamId: String): Flow<List<PlayerWithCheerSongs>>

    // 필터 없이 팀 전체 선수
    @Transaction
    @Query("SELECT * FROM players WHERE team_id = :teamId")
    fun observeAllByTeamWithCheerSongs(teamId: String): Flow<List<PlayerWithCheerSongs>>

    @Transaction
    @Query("SELECT * FROM players WHERE player_id = :playerId")
    suspend fun getPlayerWithCheerSongs(playerId: String): PlayerWithCheerSongs?

    //  batting_order만 지우고 position/batThrow 등 다른 필드는 그대로 둠. 벤치로 내려간 선수 데이터가 보존됩니다.
    @Query("UPDATE players SET batting_order = NULL WHERE team_id = :teamId AND batting_order IS NOT NULL")
    suspend fun clearBattingOrder(teamId: String)

    // PK가 이미 있으면 전체 컬럼을 새 값으로 갱신합니다 (부분 업데이트 아님).
    // syncLineup에서 이 함수가 호출되면 선발 선수의 position/batThrow도 같이 최신화됩니다.
    @Upsert
    suspend fun upsertAll(players: List<PlayerEntity>)

    @Upsert
    suspend fun upsert(player: PlayerEntity)

    // syncAllPlayers의 완전 교체 전략에서 사용. FK CASCADE라 cheer_songs도 같이 삭제됩니다.
    @Query("DELETE FROM players WHERE team_id = :teamId")
    suspend fun deleteAllByTeam(teamId: String)
}
