package com.gms.cheerlotandroid.data.storage.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey
    @ColumnInfo(name = "team_id")
    val teamId: String,
    @ColumnInfo(name = "has_today_game")
    val hasTodayGame: Boolean = false,
    @ColumnInfo(name = "opponent_team_id")
    val opponentTeamId: String? = null,
    @ColumnInfo(name = "starter_pitcher_name")
    val starterPitcherName: String? = null,
    @ColumnInfo(name = "last_game_date")
    val lastGameDate: String? = null,
    @ColumnInfo(name = "lineup_updated_today")
    val lineupUpdatedToday: Boolean = false,
    @ColumnInfo(name = "is_season_ended")
    val isSeasonEnded: Boolean = false,
    // 라인업이 마지막으로 확정됐을 때(refreshGameInfo 시점)의 스케줄 기준 홈/원정 여부 스냅샷입니다.
    // recentGames는 매일 갱신되지만 이 값은 게임정보가 갱신될 때만 같이 저장됩니다.
    @ColumnInfo(name = "is_home")
    val isHome: Boolean? = null,
    @ColumnInfo(name = "recent_games")
    val recentGames: List<TeamRecentGameLocalDto> = emptyList(),
    @ColumnInfo(name = "lineup_version")
    val lineupVersion: Int = -1,
    @ColumnInfo(name = "players_version")
    val playersVersion: Int = -1
)
