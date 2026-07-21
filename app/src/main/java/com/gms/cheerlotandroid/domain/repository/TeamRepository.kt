package com.gms.cheerlotandroid.domain.repository

import com.gms.cheerlotandroid.domain.model.team.GameScheduleInfo
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamVersionInfo
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    // 로컬에 저장된 버전 스냅샷. 팀 row가 없으면 null.
    suspend fun getLocalVersions(teamId: TeamId): TeamVersionInfo?

    // 서버에서 팀 version 조회
    suspend fun fetchRemoteVersions(teamId: TeamId): TeamVersionInfo

    // I/O 없는 순수 비교 함수. local이 null이거나 lineupVersion이 다르면 true.
    fun isLineupSyncNeeded(local: TeamVersionInfo?, remote: TeamVersionInfo): Boolean

    // I/O 없는 순수 비교 함수. local이 null이거나 playersVersion이 다르면 true.
    fun isPlayersSyncNeeded(local: TeamVersionInfo?, remote: TeamVersionInfo): Boolean

    // 라인업 동기화를 마친 뒤 버전 기록만 갱신할 때 호출합니다.
    suspend fun markLineupSynced(teamId: TeamId, version: Int)

    //전체 로스터 동기화를 마친 뒤 버전 기록만 갱신할 때 호출합니다.
    suspend fun markPlayersSynced(teamId: TeamId, version: Int)

    // 로컬 오늘 경기 정보를 관찰만 합니다.
    fun observeGameInfo(teamId: TeamId): Flow<TeamGameInfo?>

    // 서버에서 경기 정보를 무조건 호출해 저장합니다.
    // 호출 시점의 스케줄 기준 isHome도 함께 스냅샷으로 저장합니다.
    suspend fun refreshGameInfo(teamId: TeamId): TeamGameInfo

    // 로컬 스케줄(최근/예정 3경기)을 관찰만 합니다.
    fun observeGameSchedule(teamId: TeamId): Flow<List<GameScheduleInfo>>

    // 캐시된 스케줄의 첫 경기 날짜가 오늘이 아닐 때만(또는 forceRefresh=true) 서버에서 불러 저장합니다.
    suspend fun syncGameSchedule(teamId: TeamId, forceRefresh: Boolean = false): List<GameScheduleInfo>

    // teams row가 없으면 만들어서, 이 팀을 FK로 참조하는 다른 테이블(players 등) 쓰기가 항상 성공하게 보장합니다.
    suspend fun ensureTeamRow(teamId: TeamId)
}
