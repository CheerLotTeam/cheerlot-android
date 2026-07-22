package com.gms.cheerlotandroid.domain.usecase.lineup

import com.gms.cheerlotandroid.domain.model.team.GameScheduleInfo
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.LineupGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// 자체적으로 동기화를 트리거하지 않습니다 — 1. GetTeamGameScheduleUseCase(스케줄)와 2. GetLineupUseCase(게임정보)가 이미 최신화해뒀다는 전제로 관찰만 조합합니다.
class GetLineupGameInfoUseCase(
    private val teamRepository: TeamRepository
) {
    operator fun invoke(teamId: TeamId): Flow<LineupGameInfo?> {
        return combine(
            teamRepository.observeGameInfo(teamId),
            teamRepository.observeGameSchedule(teamId),
        ) { recentGameInfo, schedule ->
            recentGameInfo?.let { buildLineupGameInfo(it, schedule.firstOrNull()) }
        }
    }

    private fun buildLineupGameInfo(
        recentGameInfo: TeamGameInfo,
        preview: GameScheduleInfo?
    ): LineupGameInfo {
        val gameInfo = if (recentGameInfo.status == GameStatus.PLAYING_TODAY) {
            recentGameInfo
        } else {
            recentGameInfo.copy(
                opponentTeamId = preview?.opponentTeamId,
                starterPitcherName = preview?.starterPitcherName,
                isHome = preview?.isHome
                // lastGameDate는 "마지막 확정 경기 날짜"라는 recentGameInfo의 정체성이라 덮어쓰지 않습니다.
            )
        }

        return LineupGameInfo(gameInfo = gameInfo, recentGameInfo = recentGameInfo)
    }
}
