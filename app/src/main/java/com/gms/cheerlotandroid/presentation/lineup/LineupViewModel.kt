package com.gms.cheerlotandroid.presentation.lineup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamGameScheduleUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamUseCase
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class LineupViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getLineupUseCase: GetLineupUseCase,
    private val getLineupGameInfoUseCase: GetLineupGameInfoUseCase,
    private val getTeamGameScheduleUseCase: GetTeamGameScheduleUseCase,
    private val getTeamUseCase: GetTeamUseCase
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

    val uiState: StateFlow<LineupUiState> = getSelectedTeamUseCase()
        .distinctUntilChanged()
        .flatMapLatest { teamId ->
            // ViewModel이 유지된 상태로 팀을 변경해도 이전 팀의 일시적인 UI 상태를 넘기지 않습니다.
            showLineupOverride.value = false
            toastState.value = ToastState()
            errorMessage.value = null
            isLoading.value = teamId != null
            isRefreshing.value = false

            if (teamId == null) {
                flowOf(LineupUiState(teamId = null, isLoading = false))
            } else {
                val team = getTeamUseCase(teamId)
                val teamEnglishName = team?.englishFullName ?: ""
                val teamShortName = team?.shortName ?: ""

                merge(
                    flowOf(false),
                    refreshRequests
                        .filter { requestedTeamId -> requestedTeamId == teamId }
                        .map { true }
                ).flatMapLatest { forceRefresh ->
                    flow {
                        isLoading.value = true
                        errorMessage.value = null

                        // 새로고침도 최초 로드와 동일한 순서를 사용해 실패 후 관찰을 다시 시작합니다.
                        getTeamGameScheduleUseCase(teamId, forceRefresh)
                        val playersFlow = getLineupUseCase(teamId, forceRefresh)
                        val gameInfoFlow = getLineupGameInfoUseCase(teamId)
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
                                    isLoading = loading,
                                    errorMessage = error
                                )
                            }
                        )
                    }
                }
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
                LineupTapAction.GoToPlayback(flatSongIndex(player.cheerSongs.first()))
            }

            else -> LineupTapAction.ShowSongList(player)
        }
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
        val flatSongs = uiState.value.players.flatMap { it.cheerSongs }
        return flatSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
    }
}
