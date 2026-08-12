package com.gms.cheerlotandroid.domain.usecase.team

import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 선택 팀이 오늘 경기 중이거나 라인업 발표 대기 상태인지 판단합니다.
class IsGameDayUseCase(
    private val getLineupGameInfoUseCase: GetLineupGameInfoUseCase,
) {
    operator fun invoke(teamId: TeamId): Flow<Boolean> =
        getLineupGameInfoUseCase(teamId).map { gameInfo ->
            when (gameInfo?.gameInfo?.status) {
                GameStatus.PLAYING_TODAY,
                GameStatus.LINEUP_PENDING -> true

                else -> false
            }
        }
}
