package com.gms.cheerlotandroid.data.repository

import com.gms.cheerlotandroid.data.mapper.toDomain
import com.gms.cheerlotandroid.data.mapper.toDomainList
import com.gms.cheerlotandroid.data.mapper.toGameInfo
import com.gms.cheerlotandroid.data.mapper.toLocalDtoList
import com.gms.cheerlotandroid.data.mapper.toVersionInfo
import com.gms.cheerlotandroid.data.network.service.TeamApiService
import com.gms.cheerlotandroid.data.network.safeApiCall
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.data.storage.local.dao.TeamDao
import com.gms.cheerlotandroid.data.storage.local.entity.TeamEntity
import com.gms.cheerlotandroid.domain.model.team.GameScheduleInfo
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamVersionInfo
import com.gms.cheerlotandroid.domain.repository.TeamRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TeamRepositoryImpl(
    private val teamDao: TeamDao,
    private val teamApiService: TeamApiService,
    private val teamCatalog: TeamCatalog = TeamCatalog
) : TeamRepository {

    override suspend fun getLocalVersions(teamId: TeamId): TeamVersionInfo? {
        return teamDao.getTeam(teamId.value)?.toVersionInfo()
    }

    override suspend fun fetchRemoteVersions(teamId: TeamId): TeamVersionInfo {
        val dto = safeApiCall { teamApiService.getTeamVersions(teamCatalog.toApiCode(teamId)) }
        return dto.toDomain(teamId)
    }

    override fun isLineupSyncNeeded(local: TeamVersionInfo?, remote: TeamVersionInfo): Boolean {
        return local == null || local.lineupVersion != remote.lineupVersion
    }

    override fun isPlayersSyncNeeded(local: TeamVersionInfo?, remote: TeamVersionInfo): Boolean {
        return local == null || local.playersVersion != remote.playersVersion
    }

    override suspend fun markLineupSynced(teamId: TeamId, version: Int) {
        ensureTeamRow(teamId)
        teamDao.updateLineupVersion(teamId.value, version)
    }

    override suspend fun markPlayersSynced(teamId: TeamId, version: Int) {
        ensureTeamRow(teamId)
        teamDao.updatePlayersVersion(teamId.value, version)
    }

    override fun observeGameInfo(teamId: TeamId): Flow<TeamGameInfo?> {
        return teamDao.observeTeam(teamId.value).map { it?.toGameInfo() }
    }

    override suspend fun refreshGameInfo(teamId: TeamId): TeamGameInfo {
        val dto = safeApiCall { teamApiService.getTeamTodayGameInfo(teamCatalog.toApiCode(teamId)) }
        ensureTeamRow(teamId)

        // 오늘 경기 정보가 갱신되는 이 순간의 스케줄 기준 홈/원정 여부를 같이 저장
        // recentGames는 매일 갱신되므로, 여기서 스냅샷을 남겨두지 않으면 이후 스케줄이 다음 날짜로 넘어갔을 때 "이 게임정보가 확정된 시점의 isHome"을 복구할 수 없습니다.
        val isHome = teamDao.getTeam(teamId.value)?.recentGames?.firstOrNull()?.isHome

        teamDao.updateGameInfo(
            teamId = teamId.value,
            hasTodayGame = dto.hasTodayGame,
            opponentTeamId = dto.opponentTeamCode?.let { teamCatalog.findByApiCode(it)?.id?.value },
            starterPitcherName = dto.starterPitcherName,
            lastGameDate = dto.lastGameDate,
            lineupUpdatedToday = dto.lineupUpdatedToday,
            isSeasonEnded = dto.isSeasonEnded,
            isHome = isHome
        )
        return dto.toDomain(teamId).copy(isHome = isHome)
    }

    override fun observeGameSchedule(teamId: TeamId): Flow<List<GameScheduleInfo>> {
        return teamDao.observeTeam(teamId.value).map { it?.recentGames.orEmpty().toDomainList() }
    }

    override suspend fun syncGameSchedule(teamId: TeamId, forceRefresh: Boolean): List<GameScheduleInfo> {
        val cached = teamDao.getTeam(teamId.value)?.recentGames.orEmpty()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val isFresh = cached.firstOrNull()?.date == today
        if (!forceRefresh && isFresh) {
            return cached.toDomainList()
        }

        val dto = safeApiCall { teamApiService.getTeamGamesInfo(teamCatalog.toApiCode(teamId)) }
        val localDtos = dto.toLocalDtoList()
        ensureTeamRow(teamId)
        teamDao.updateRecentGames(teamId.value, localDtos)
        return localDtos.toDomainList()
    }

    // teams row가 없으면 만들어서 FK 없이도 UPDATE가 항상 성공하게 보장
    private suspend fun ensureTeamRow(teamId: TeamId) {
        teamDao.insertIfAbsent(TeamEntity(teamId = teamId.value))
    }
}
