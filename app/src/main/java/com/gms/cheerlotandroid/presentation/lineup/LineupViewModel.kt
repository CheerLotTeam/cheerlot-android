package com.gms.cheerlotandroid.presentation.lineup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupGameInfoUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.GetLineupUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamGameScheduleUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetTeamUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    // 팀 전환 중 화면에 계속 떠 있는 상태라면 초기화되지 않지만, 팀 변경은 보통 이 화면을 벗어나서
    // 이뤄지므로(설정 > 팀 변경) ViewModel도 함께 재생성되는 게 일반적인 경로입니다.
    private val showLineupOverride = MutableStateFlow(false)
    private val toastState = MutableStateFlow(ToastState())
    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<LineupUiState> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(LineupUiState(teamId = null))
            } else {
                val team = getTeamUseCase(teamId)
                val teamEnglishName = team?.englishFullName ?: ""
                val teamShortName = team?.shortName ?: ""

                flow {
                    // 스케줄 동기화가 끝난 뒤 라인업을 동기화해야 게임 정보 조합에 최신 값이 전달됩니다.
                    getTeamGameScheduleUseCase(teamId)
                    val playersFlow = getLineupUseCase(teamId)
                    val gameInfoFlow = getLineupGameInfoUseCase(teamId)

                    emitAll(
                        combine(
                            playersFlow,
                            gameInfoFlow,
                            showLineupOverride,
                            toastState,
                            isRefreshing
                        ) { players, gameInfo, override, toast, refreshing ->
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
                                toastMessage = toast.message,
                                isToastVisible = toast.isVisible,
                                isRefreshing = refreshing
                            )
                        }
                    )
                }.catch { throwable ->
                    emit(
                        LineupUiState(
                            teamId = teamId,
                            teamEnglishName = teamEnglishName,
                            teamShortName = teamShortName,
                            errorMessage = throwable.message ?: "라인업을 불러오지 못했습니다."
                        )
                    )
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

    fun refresh() {
        val teamId = uiState.value.teamId ?: return
        if (isRefreshing.value) return

        isRefreshing.value = true
        viewModelScope.launch {
            try {
                getTeamGameScheduleUseCase(teamId, forceRefresh = true)
                getLineupUseCase(teamId, forceRefresh = true)
                getLineupGameInfoUseCase(teamId).first()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                toastState.value = ToastState(
                    message = throwable.message ?: "라인업을 새로고침하지 못했습니다.",
                    isVisible = true
                )
            } finally {
                isRefreshing.value = false
            }
        }
    }

    // 모든 선수의 응원가를 타순대로 이어붙였을 때 특정 곡이 몇 번째인지 계산합니다 (캐러셀 인덱스용)
    private fun flatSongIndex(song: CheerSongInfo): Int {
        val flatSongs = uiState.value.players.flatMap { it.cheerSongs }
        return flatSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
    }
}
