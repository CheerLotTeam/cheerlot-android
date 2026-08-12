package com.gms.cheerlotandroid.presentation.lineup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.player.GetAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.playback.PlayLineupSongsUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamGameScheduleUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamUseCase
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsEvent
import com.gms.cheerlotandroid.domain.service.analytics.AnalyticsService
import com.gms.cheerlotandroid.domain.service.analytics.AppEntryPoint
import com.gms.cheerlotandroid.domain.model.team.GameStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

private val seoulZoneId: ZoneId = ZoneId.of("Asia/Seoul")

@OptIn(ExperimentalCoroutinesApi::class)
class LineupViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
    private val getLineupUseCase: GetLineupUseCase,
    private val getLineupGameInfoUseCase: GetLineupGameInfoUseCase,
    private val getTeamGameScheduleUseCase: GetTeamGameScheduleUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val playLineupSongsUseCase: PlayLineupSongsUseCase,
    private val analyticsService: AnalyticsService,
    private val currentDateProvider: () -> LocalDate = { LocalDate.now(seoulZoneId) }
) : ViewModel() {

    private data class ToastState(
        val message: String = "",
        val isVisible: Boolean = false
    )

    private data class FeedbackState(
        val toast: ToastState,
        val errorMessage: String?,
        val isLoading: Boolean,
        val isRefreshing: Boolean
    )

    private val showLineupOverride = MutableStateFlow(false)
    private val toastState = MutableStateFlow(ToastState())
    private val errorMessage = MutableStateFlow<String?>(null)
    private val isLoading = MutableStateFlow(true)
    private val isRefreshing = MutableStateFlow(false)
    private val refreshRequests = MutableSharedFlow<TeamId>(extraBufferCapacity = 1)
    private var hasHandledTeam = false
    private var lastTeamId: TeamId? = null
    private var hasTrackedAppOpen = false

    val uiState: StateFlow<LineupUiState> = getSelectedTeamUseCase()
        .distinctUntilChanged()
        .flatMapLatest { teamId ->
            // 선택 팀이 실제로 변경된 경우에만 이전 팀의 UI 상태를 초기화합니다.
            if (!hasHandledTeam || lastTeamId != teamId) {
                hasHandledTeam = true
                lastTeamId = teamId
                showLineupOverride.value = false
                toastState.value = ToastState()
                errorMessage.value = null
                isLoading.value = teamId != null
                isRefreshing.value = false
            }

            if (teamId == null) {
                flowOf(LineupUiState(teamId = null, isLoading = false))
            } else {
                val team = getTeamUseCase(teamId)
                val teamEnglishName = team?.englishFullName ?: ""
                val teamShortName = team?.shortName ?: ""

                merge(
                    flowOf(Unit),
                    refreshRequests
                        .filter { requestedTeamId -> requestedTeamId == teamId }
                        .map { Unit }
                ).flatMapLatest {
                    flow {
                        isLoading.value = true
                        errorMessage.value = null

                        // 새로고침도 최초 로드와 동일한 순서를 사용해 실패 후 관찰을 다시 시작합니다.
                        getTeamGameScheduleUseCase(teamId)
                        getAllPlayersUseCase(teamId)
                        val playersFlow = getLineupUseCase(teamId)
                        val gameInfoFlow = getLineupGameInfoUseCase(teamId)
                        val todayDate = currentDateProvider()
                        isLoading.value = false
                        isRefreshing.value = false

                        val feedbackFlow = combine(
                            toastState,
                            errorMessage,
                            isLoading,
                            isRefreshing
                        ) { toast, error, loading, refreshing ->
                            FeedbackState(toast, error, loading, refreshing)
                        }

                        emitAll(
                            combine(
                                playersFlow,
                                gameInfoFlow,
                                showLineupOverride,
                                feedbackFlow
                            ) { players, gameInfo, override, feedback ->
                                val opponentTeamName =
                                    gameInfo?.gameInfo?.opponentTeamId?.let { opponentId ->
                                        getTeamUseCase(opponentId)?.shortName
                                    }
                                val recentOpponentTeamName =
                                    gameInfo?.recentGameInfo?.opponentTeamId?.let { opponentId ->
                                        getTeamUseCase(opponentId)?.shortName
                                    }

                                LineupUiState(
                                    teamId = teamId,
                                    teamEnglishName = teamEnglishName,
                                    teamShortName = teamShortName,
                                    opponentTeamName = opponentTeamName,
                                    recentOpponentTeamName = recentOpponentTeamName,
                                    players = players,
                                    gameInfo = gameInfo,
                                    todayDate = todayDate,
                                    showLineupOverride = override,
                                    toastMessage = feedback.toast.message,
                                    isToastVisible = feedback.toast.isVisible,
                                    isLoading = feedback.isLoading,
                                    isRefreshing = feedback.isRefreshing,
                                    errorMessage = feedback.errorMessage
                                )
                            }
                        )
                    }.catch { throwable ->
                        errorMessage.value = throwable.message
                            ?: "라인업을 불러오지 못했습니다."
                        isLoading.value = false
                        isRefreshing.value = false

                        emitAll(
                            combine(errorMessage, isLoading) { error, loading ->
                                LineupUiState(
                                    teamId = teamId,
                                    teamEnglishName = teamEnglishName,
                                    teamShortName = teamShortName,
                                    todayDate = currentDateProvider(),
                                    isLoading = loading,
                                    errorMessage = error
                                )
                            }
                        )
                    }
                }
            }
        }
        // app_open의 is_game_day를 확정하기 위해 라인업의 최초 데이터 로드 완료 시 한 번만 기록합니다.
        .onEach { state ->
            if (!hasTrackedAppOpen && !state.isLoading && state.teamId != null) {
                hasTrackedAppOpen = true
                val status = state.gameInfo?.gameInfo?.status
                analyticsService.track(
                    AnalyticsEvent.AppOpen(
                        entryPoint = AppEntryPoint.APP,
                        isGameDay = status == GameStatus.PLAYING_TODAY ||
                            status == GameStatus.LINEUP_PENDING,
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LineupUiState()
        )

    fun toggleShowLineup() {
        showLineupOverride.update { true }
    }

    fun onPlayerTap(player: PlayerInfo): LineupTapAction {
        return when {
            player.cheerSongs.isEmpty() -> {
                toastState.update {
                    ToastState(
                        message = "아직 개인 응원가가 없어요",
                        isVisible = true
                    )
                }
                LineupTapAction.ShowNoSongToast
            }

            player.cheerSongs.size == 1 -> {
                val startIndex = flatSongIndex(player.cheerSongs.first())
                startPlayback(startAt = startIndex)
                LineupTapAction.GoToPlayback(startIndex)
            }

            else -> {
                val (songs, playerNames) = flatSongsAndNames()
                LineupTapAction.ShowSongList(
                    member = player,
                    startIndex = flatSongIndex(player.cheerSongs.first()),
                    queueSongs = songs,
                    queuePlayerNames = playerNames,
                    queuePlayerIds = uiState.value.players.flatMap { lineupPlayer ->
                        lineupPlayer.cheerSongs.map { lineupPlayer.id.value }
                    },
                    isGameDay = uiState.value.gameStatus == GameStatus.PLAYING_TODAY ||
                        uiState.value.gameStatus == GameStatus.LINEUP_PENDING,
                )
            }
        }
    }

    private fun startPlayback(startAt: Int) {
        val teamId = uiState.value.teamId ?: return
        val (songs, playerNames) = flatSongsAndNames()
        if (songs.isEmpty()) return
        playLineupSongsUseCase(
            songs = songs,
            playerNames = playerNames,
            startAt = startAt,
            teamId = teamId,
            playerIds = uiState.value.players.flatMap { player ->
                player.cheerSongs.map { player.id.value }
            },
            isGameDay = uiState.value.gameStatus == GameStatus.PLAYING_TODAY ||
                uiState.value.gameStatus == GameStatus.LINEUP_PENDING,
        )
    }

    // 모든 선수의 응원가를 타순대로 이어붙인 (곡, 선수명) 리스트입니다. 선수명은 곡 개수만큼 반복됩니다.
    private fun flatSongsAndNames(): Pair<List<CheerSongInfo>, List<String>> {
        val songs = uiState.value.players.flatMap { player -> player.cheerSongs.map { it to player.name } }
        return songs.map { it.first } to songs.map { it.second }
    }

    fun dismissToast() {
        toastState.update { it.copy(isVisible = false) }
    }

    fun dismissError() {
        errorMessage.value = null
    }

    fun refresh() {
        val teamId = uiState.value.teamId ?: return
        if (isLoading.value) return

        isLoading.value = true
        isRefreshing.value = true
        viewModelScope.launch {
            refreshRequests.emit(teamId)
        }
    }

    // 모든 선수의 응원가를 타순대로 이어붙였을 때 특정 곡이 몇 번째인지 계산합니다 (캐러셀 인덱스용)
    private fun flatSongIndex(song: CheerSongInfo): Int {
        val (songs, _) = flatSongsAndNames()
        return songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
    }
}
