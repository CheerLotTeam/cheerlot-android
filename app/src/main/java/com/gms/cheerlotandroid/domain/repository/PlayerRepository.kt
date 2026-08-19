package com.gms.cheerlotandroid.domain.repository

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    // 로컬 라인업(batting_order != null)만 관찰. SQL에서 이미 필터/정렬됨.
    fun observeLineup(teamId: TeamId): Flow<List<PlayerInfo>>

    // 로컬 전체 로스터를 관찰.
    fun observeAllPlayers(teamId: TeamId): Flow<List<PlayerInfo>>

    // 로컬에서 타순이 없는 벤치 선수를 이름순으로 관찰합니다.
    fun observeBenchPlayers(teamId: TeamId): Flow<List<PlayerInfo>>

    // lineupVersion 비교 후 필요하면(또는 forceRefresh) 라인업 + 경기 정보 최신화.
    // 서버에서 라인업을 받아 벤치 선수 데이터는 보존한 채 선발 표시만 갱신합니다.
    suspend fun syncLineup(teamId: TeamId, forceRefresh: Boolean)

    // playersVersion 비교 후 필요하면(또는 forceRefresh) 서버에서 전체 로스터를 받아 완전 교체.
    suspend fun syncAllPlayers(teamId: TeamId, forceRefresh: Boolean)

    // 로컬 캐시에서 선수 정보 읽기.
    suspend fun getPlayerDetail(playerId: String): PlayerInfo

    // 선발 선수의 타순을 벤치 선수에게 넘기고 선발 선수는 벤치로 이동시킵니다.
    suspend fun swapLineupPlayer(
        teamId: TeamId,
        lineupPlayerId: String,
        benchPlayerId: String,
        battingOrder: Int
    )
}
