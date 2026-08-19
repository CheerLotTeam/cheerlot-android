package com.gms.cheerlotandroid.data.storage.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gms.cheerlotandroid.data.storage.local.entity.TeamEntity
import com.gms.cheerlotandroid.data.storage.local.entity.TeamRecentGameLocalDto
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    // 1회성 스냅샷. "지금 값 하나"만 필요할 때 사용.
    @Query("SELECT * FROM teams WHERE team_id = :teamId")
    suspend fun getTeam(teamId: String): TeamEntity?

    // teams 테이블에 쓰기가 생길 때마다 Room이 자동으로 재실행해 새 값을 흘려보내는 관찰용 Flow.
    @Query("SELECT * FROM teams WHERE team_id = :teamId")
    fun observeTeam(teamId: String): Flow<TeamEntity?>

    // team_id row가 없을 때만 기본값으로 생성.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(team: TeamEntity)

    // 버전 컬럼만 부분 UPDATE
    @Query("UPDATE teams SET lineup_version = :version WHERE team_id = :teamId")
    suspend fun updateLineupVersion(teamId: String, version: Int)

    @Query("UPDATE teams SET players_version = :version WHERE team_id = :teamId")
    suspend fun updatePlayersVersion(teamId: String, version: Int)

    // recent_games 컬럼만 부분 UPDATE.
    @Query("UPDATE teams SET recent_games = :recentGames WHERE team_id = :teamId")
    suspend fun updateRecentGames(teamId: String, recentGames: List<TeamRecentGameLocalDto>)

    // 오늘 경기 정보 관련 컬럼을 한 번에 부분 UPDATE.
    @Query(
        """
        UPDATE teams SET
            has_today_game = :hasTodayGame,
            opponent_team_id = :opponentTeamId,
            starter_pitcher_name = :starterPitcherName,
            last_game_date = :lastGameDate,
            lineup_updated_today = :lineupUpdatedToday,
            is_season_ended = :isSeasonEnded,
            is_home = :isHome
        WHERE team_id = :teamId
        """
    )
    suspend fun updateGameInfo(
        teamId: String,
        hasTodayGame: Boolean,
        opponentTeamId: String?,
        starterPitcherName: String?,
        lastGameDate: String?,
        lineupUpdatedToday: Boolean,
        isSeasonEnded: Boolean,
        isHome: Boolean?
    )
}
