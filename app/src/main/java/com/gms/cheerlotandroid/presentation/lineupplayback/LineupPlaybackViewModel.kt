package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.LineupGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamGameInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.PlaySource
import com.gms.cheerlotandroid.domain.service.analytics.PlayViewType
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.ObserveLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

// 재생 화면 전용 ViewModel입니다. 실제 재생 시작은 LineupViewModel(선수 탭)이 이미 해뒀다는 전제로,
// 여기서는 audioPlayer.state를 관찰만 하고 화면에 보여줄 라인업/경기 정보만 별도로 다시 읽어옵니다.
@OptIn(ExperimentalCoroutinesApi::class)
internal class LineupPlaybackViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val observeLineupUseCase: ObserveLineupUseCase,
    private val getLineupGameInfoUseCase: GetLineupGameInfoUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val audioPlayer: AudioPlayer,
    private val analyticsService: AnalyticsService,
) : ViewModel() {

    private data class ScreenInfo(
        val teamId: TeamId,
        val teamShortName: String,
        val opponentTeamName: String?,
        val gameInfo: TeamGameInfo?,
        val items: List<LineupPlaybackItem>
    )

    private val screenInfoFlow: Flow<ScreenInfo?> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf<ScreenInfo?>(null)
            } else {
                // 재동기화 없이 로컬 라인업만 관찰합니다(메인 라인업 화면이 이미 동기화).
                // 오프라인에서 셀을 눌러 진입해도 네트워크 예외로 죽지 않습니다.
                combine(
                    observeLineupUseCase(teamId),
                    getLineupGameInfoUseCase(teamId)
                ) { players, gameInfo -> buildScreenInfo(teamId, players, gameInfo) }
            }
        }
        // 로컬 관찰이라 사실상 실패하지 않지만, 예외로 수집이 끊겨 앱이 죽지 않도록 방어합니다.
        .catch { emit(null) }

    val uiState: StateFlow<LineupPlaybackUiState> = combine(
        screenInfoFlow,
        audioPlayer.state
    ) { screenInfo, playbackState ->
        if (screenInfo == null || screenInfo.items.isEmpty()) {
            LineupPlaybackUiState(isLoading = true)
        } else {
            LineupPlaybackUiState(
                teamId = screenInfo.teamId,
                teamShortName = screenInfo.teamShortName,
                opponentTeamName = screenInfo.opponentTeamName,
                gameInfo = screenInfo.gameInfo,
                items = screenInfo.items,
                currentPlaybackIndex = playbackState.currentQueueIndex.coerceIn(screenInfo.items.indices),
                isPlaying = playbackState.isPlaying,
                isLoading = false
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LineupPlaybackUiState())

    fun onTogglePlayback() {
        audioPlayer.toggle()
    }

    // iOS의 LineupPlaybackService.stop()과 동일하게, 이 화면을 나가면(닫기/뒤로가기 등 모든 경로)
    // 재생을 완전히 끊습니다. 라인업 재생은 미니플레이어로 이어지지 않는 Shorts 방식이라,
    // 화면만 닫는 BasePlayback(PlaybackViewModel.close)과 달리 stop으로 큐 자체를 비웁니다.
    fun stopPlayback() {
        val state = audioPlayer.state.value
        analyticsService.track(
            AnalyticsEvent.PlayViewDismissed(
                source = PlaySource.LINEUP,
                viewType = PlayViewType.LINEUP_PLAYBACK,
                isPlaying = state.isPlaying,
                isGameDay = state.isGameDay,
                playerId = state.currentPlayerId,
            )
        )
        audioPlayer.stop()
    }

    fun trackPresented() {
        val state = audioPlayer.state.value
        analyticsService.track(
            AnalyticsEvent.PlayViewPresented(
                source = PlaySource.LINEUP,
                viewType = PlayViewType.LINEUP_PLAYBACK,
                isPlaying = state.isPlaying,
                isGameDay = state.isGameDay,
                playerId = state.currentPlayerId,
            )
        )
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
        // 재생 중인 카드는 로컬에 저장된 최근 확정 라인업이므로 TopBar도 같은 경기 정보를 사용합니다.
        // 오늘 라인업이 확정된 경우에는 recentGameInfo와 gameInfo가 동일합니다.
        val displayGameInfo = gameInfo?.recentGameInfo
        val opponentTeamName = displayGameInfo?.opponentTeamId?.let { getTeamUseCase(it)?.shortName }
        val teamShortName = team?.shortName.orEmpty()

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
            teamShortName = teamShortName,
            opponentTeamName = opponentTeamName,
            gameInfo = displayGameInfo,
            items = items
        )
    }
}
