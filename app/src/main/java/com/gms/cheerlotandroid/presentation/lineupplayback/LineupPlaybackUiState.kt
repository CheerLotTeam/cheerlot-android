package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.compose.runtime.Immutable
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Immutable
internal data class LineupPlaybackItem(
    val id: String,
    val battingOrder: Int,
    val memberName: String,
    val cheerSongTitle: String,
    val lyrics: String
)

@Immutable
internal data class LineupPlaybackUiState(
    val teamId: TeamId? = null,
    val teamShortName: String = "",
    val opponentTeamName: String? = null,
    val gameInfo: TeamGameInfo? = null,
    val items: List<LineupPlaybackItem> = emptyList(),
    val currentPlaybackIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true
) {
    val gameDate: String
        get() = gameInfo?.lastGameDate.toKoreanDate()

    val teamsText: String
        get() {
            val opponent = opponentTeamName ?: return "경기없음"
            return if (gameInfo?.isHome == true) {
                "$opponent vs $teamShortName"
            } else {
                "$teamShortName vs $opponent"
            }
        }

    private fun String?.toKoreanDate(): String {
        val date = this ?: return ""
        return runCatching {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).format(koreanDateFormatter)
        }.getOrDefault("")
    }

    private companion object {
        val koreanDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
    }
}
