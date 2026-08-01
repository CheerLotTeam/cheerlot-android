package com.gms.cheerlotandroid.presentation.lineup

import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import com.gms.cheerlotandroid.domain.model.team.LineupGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class LineupUiState(
    val teamId: TeamId? = null,
    val teamEnglishName: String = "",
    val teamShortName: String = "",
    val opponentTeamName: String? = null,
    val recentOpponentTeamName: String? = null,
    val players: List<PlayerInfo> = emptyList(),
    val gameInfo: LineupGameInfo? = null,
    val showLineupOverride: Boolean = false,
    val toastMessage: String = "",
    val isToastVisible: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
) {
    val gameStatus: GameStatus
        get() = gameInfo?.gameInfo?.status ?: GameStatus.OFF_DAY

    // 오늘 경기 중이거나, 사용자가 "최근 라인업 보기"를 눌렀으면 노출.
    val shouldShowLineup: Boolean
        get() = gameStatus == GameStatus.PLAYING_TODAY || showLineupOverride

    val displayGameInfoText: String
        get() {
            val selectedGameInfo = if (showLineupOverride) {
                gameInfo?.recentGameInfo
            } else {
                gameInfo?.gameInfo
            } ?: return ""
            val date = if (showLineupOverride) {
                selectedGameInfo.lastGameDate.toKoreanDate()
            } else {
                LocalDate.now(seoulZoneId).format(koreanDateFormatter)
            }
            val opponent = if (showLineupOverride) {
                recentOpponentTeamName
            } else {
                opponentTeamName
            }
            val teams = opponent?.let {
                if (selectedGameInfo.isHome == true) "$it vs $teamShortName" else "$teamShortName vs $it"
            } ?: "경기없음"
            return "$date | $teams"
        }

    val displayStarterPitcherName: String?
        get() {
            if (!shouldShowLineup && gameStatus != GameStatus.LINEUP_PENDING) return null
            return if (showLineupOverride) {
                gameInfo?.recentGameInfo?.starterPitcherName
            } else {
                gameInfo?.gameInfo?.starterPitcherName
            }
        }

    private fun String?.toKoreanDate(): String {
        val date = this ?: return ""
        return runCatching {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(koreanDateFormatter)
        }.getOrDefault("")
    }

    private companion object {
        val seoulZoneId: ZoneId = ZoneId.of("Asia/Seoul")
        val koreanDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
    }
}
