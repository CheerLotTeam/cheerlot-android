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
    @ColumnInfo(name = "lineup_version")
    val lineupVersion: Int = -1,
    @ColumnInfo(name = "players_version")
    val playersVersion: Int = -1
)
