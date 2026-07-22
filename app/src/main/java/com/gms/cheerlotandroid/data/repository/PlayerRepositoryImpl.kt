package com.gms.cheerlotandroid.data.repository

import androidx.room.withTransaction
import com.gms.cheerlotandroid.data.mapper.toDomain
import com.gms.cheerlotandroid.data.mapper.toEntity
import com.gms.cheerlotandroid.data.mapper.toPlayerEntity
import com.gms.cheerlotandroid.data.network.safeApiCall
import com.gms.cheerlotandroid.data.network.service.PlayerApiService
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.data.storage.local.CheerLotDatabase
import com.gms.cheerlotandroid.data.storage.local.dao.CheerSongDao
import com.gms.cheerlotandroid.data.storage.local.dao.PlayerDao
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.PlayerRepository
import com.gms.cheerlotandroid.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val database: CheerLotDatabase,
    private val playerDao: PlayerDao,
    private val cheerSongDao: CheerSongDao,
    private val playerApiService: PlayerApiService,
    private val teamRepository: TeamRepository,
    private val teamCatalog: TeamCatalog = TeamCatalog
) : PlayerRepository {

    override fun observeLineup(teamId: TeamId): Flow<List<PlayerInfo>> {
        return playerDao.observeLineupWithCheerSongs(teamId.value)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun observeAllPlayers(teamId: TeamId): Flow<List<PlayerInfo>> {
        return playerDao.observeAllByTeamWithCheerSongs(teamId.value)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncLineup(teamId: TeamId, forceRefresh: Boolean) {
        val local = teamRepository.getLocalVersions(teamId)
        val remote = teamRepository.fetchRemoteVersions(teamId)
        if (!forceRefresh && !teamRepository.isLineupSyncNeeded(local, remote)) return

        teamRepository.refreshGameInfo(teamId)

        val dto = safeApiCall { playerApiService.getLineup(teamCatalog.toApiCode(teamId)) }
        val playerEntities = dto.players.map { it.toPlayerEntity(teamId) }
        val cheerSongEntities = dto.players.flatMap { starter ->
            starter.cheerSongs.map { it.toEntity(starter.playerCode) }
        }

        // 라인업 동기화는 팀 전체를 delete-then-insert 하지 않습니다.
        // getAllPlayers로 채워진 벤치 선수 데이터를 지우지 않기 위해, 기존 선발 표시를 초기화한 뒤 이번에 내려온 선발만 upsert합니다.
        database.withTransaction {
            playerDao.clearBattingOrder(teamId.value)
            playerDao.upsertAll(playerEntities)
            cheerSongDao.upsertAll(cheerSongEntities)
        }
        teamRepository.markLineupSynced(teamId, remote.lineupVersion)
    }

    override suspend fun syncAllPlayers(teamId: TeamId, forceRefresh: Boolean) {
        val local = teamRepository.getLocalVersions(teamId)
        val remote = teamRepository.fetchRemoteVersions(teamId)
        if (!forceRefresh && !teamRepository.isPlayersSyncNeeded(local, remote)) return

        val dto = safeApiCall { playerApiService.getAllPlayers(teamCatalog.toApiCode(teamId)) }
        val playerEntities = dto.players.map { it.toPlayerEntity(teamId) }
        val cheerSongEntities = dto.players.flatMap { player ->
            player.cheerSongs.map { it.toEntity(player.playerCode) }
        }

        // players가 team_id를 FK로 참조하므로, insert 전에 teams row 존재를 먼저 보장합니다.
        teamRepository.ensureTeamRow(teamId)

        // 전체 로스터 응답은 완전한 팀 구성을 담고 있으므로 통째로 교체해도 안전합니다.
        database.withTransaction {
            playerDao.deleteAllByTeam(teamId.value)
            playerDao.upsertAll(playerEntities)
            cheerSongDao.upsertAll(cheerSongEntities)
        }
        teamRepository.markPlayersSynced(teamId, remote.playersVersion)
    }

    override suspend fun getPlayerDetail(playerId: String): PlayerInfo {
        // 여기서는 네트워크를 타지 않고 캐시만 읽습니다.
        return playerDao.getPlayerWithCheerSongs(playerId)?.toDomain()
            ?: error("Player not found in local cache: $playerId")
    }
}
