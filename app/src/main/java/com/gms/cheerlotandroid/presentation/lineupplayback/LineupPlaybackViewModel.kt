package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.LineupGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val seoulZoneId: ZoneId = ZoneId.of("Asia/Seoul")

// 재생 화면 전용 ViewModel입니다. 실제 재생 시작은 LineupViewModel(선수 탭)이 이미 해뒀다는 전제로,
// 여기서는 audioPlayer.state를 관찰만 하고 화면에 보여줄 라인업/경기 정보만 별도로 다시 읽어옵니다.
@OptIn(ExperimentalCoroutinesApi::class)
internal class LineupPlaybackViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getLineupUseCase: GetLineupUseCase,
    private val getLineupGameInfoUseCase: GetLineupGameInfoUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val audioPlayer: AudioPlayer,
    private val currentDateProvider: () -> LocalDate = { LocalDate.now(seoulZoneId) }
) : ViewModel() {

    private data class ScreenInfo(
        val teamId: TeamId,
        val gameDate: String,
        val teamsText: String,
        val items: List<LineupPlaybackItem>
    )

    private val screenInfoFlow: Flow<ScreenInfo?> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf<ScreenInfo?>(null)
            } else {
                combine(
                    flow { emitAll(getLineupUseCase(teamId)) },
                    getLineupGameInfoUseCase(teamId)
                ) { players, gameInfo -> buildScreenInfo(teamId, players, gameInfo) }
            }
        }

    val uiState: StateFlow<LineupPlaybackUiState> = combine(
        screenInfoFlow,
        audioPlayer.state
    ) { screenInfo, playbackState ->
        if (screenInfo == null || screenInfo.items.isEmpty()) {
            LineupPlaybackUiState(isLoading = true)
        } else {
            LineupPlaybackUiState(
                teamId = screenInfo.teamId,
                gameDate = screenInfo.gameDate,
                teamsText = screenInfo.teamsText,
                items = screenInfo.items,
                startIndex = playbackState.currentQueueIndex.coerceIn(screenInfo.items.indices),
                isPlaying = playbackState.isPlaying,
                isLoading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LineupPlaybackUiState())

    fun onTogglePlayback() {
        audioPlayer.toggle()
    }

    // iOS의 LineupPlaybackService.stop()과 동일하게, 이 화면을 나가면(닫기/뒤로가기 등 모든 경로)
    // 재생을 완전히 끊습니다. 라인업 재생은 미니플레이어로 이어지지 않는 Shorts 방식이라, 다른 재생
    // 화면(PlaybackViewModel.close)처럼 resetToBeginning으로 이어가지 않고 stop으로 큐 자체를 비웁니다.
    fun stopPlayback() {
        audioPlayer.stop()
    }

    // HorizontalPager는 빠른 플링으로 여러 페이지를 한 번에 건너뛸 수 있어, playNext/playPrevious(±1) 대신
    // 임의 인덱스로 바로 이동하는 playAt을 씁니다.
    fun onPageChanged(page: Int) {
        audioPlayer.playAt(page)
    }

    private fun buildScreenInfo(
        teamId: TeamId,
        players: List<PlayerInfo>,
        gameInfo: LineupGameInfo?
    ): ScreenInfo {
        val team = getTeamUseCase(teamId)
        val opponentTeamName = gameInfo?.gameInfo?.opponentTeamId?.let { getTeamUseCase(it)?.shortName }
        val teamShortName = team?.shortName.orEmpty()
        val teamsText = opponentTeamName?.let { opponent ->
            if (gameInfo?.gameInfo?.isHome == true) "$opponent vs $teamShortName" else "$teamShortName vs $opponent"
        } ?: teamShortName

        val items = players.flatMap { player ->
            player.cheerSongs.map { song ->
                LineupPlaybackItem(
                    id = "${player.id.value}-${song.id}",
                    battingOrder = player.battingOrder ?: 0,
                    memberName = player.name,
                    cheerSongTitle = song.title,
                    lyrics = song.lyrics
                )
            }
        }

        return ScreenInfo(
            teamId = teamId,
            gameDate = currentDateProvider().format(koreanDateFormatter),
            teamsText = teamsText,
            items = items
        )
    }

    private companion object {
        val koreanDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)
    }
}
