package com.gms.cheerlotandroid.presentation.teammembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.player.GetAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.playback.PlayTeamMembersUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal data class TeamMembersUiState(
    val teamId: TeamId? = null,
    val rows: List<TeamMembersRow> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String = "",
    val isToastVisible: Boolean = false
) {
    val totalSongCount: Int get() = rows.count { it.hasSong }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class TeamMembersViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
    private val playTeamMembersUseCase: PlayTeamMembersUseCase
) : ViewModel() {

    private data class ToastState(
        val message: String = "",
        val isVisible: Boolean = false
    )

    private val refreshCount = MutableStateFlow(0)
    private val toastState = MutableStateFlow(ToastState())

    private val contentState = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(TeamMembersUiState())
            } else {
                refreshCount.flatMapLatest { refreshIndex ->
                    flow { emitAll(getAllPlayersUseCase(teamId)) }
                        .map { players -> TeamMembersUiState(teamId = teamId, rows = players.toTeamMembersRows()) }
                        .onStart {
                            emit(
                                TeamMembersUiState(
                                    teamId = teamId,
                                    isLoading = refreshIndex == 0,
                                    isRefreshing = refreshIndex > 0
                                )
                            )
                        }
                        .catch { throwable ->
                            emit(
                                TeamMembersUiState(
                                    teamId = teamId,
                                    errorMessage = throwable.message ?: "선수 목록을 불러오지 못했습니다."
                                )
                            )
                        }
                }
            }
        }

    val uiState: StateFlow<TeamMembersUiState> = combine(contentState, toastState) { state, toast ->
        state.copy(toastMessage = toast.message, isToastVisible = toast.isVisible)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamMembersUiState())

    fun refresh() {
        refreshCount.value += 1
    }

    fun onTapPlayAll() {
        val state = uiState.value
        state.teamId?.let { playTeamMembersUseCase.playAll(state.rows, it) }
    }

    fun onTapSong(row: TeamMembersRow) {
        val teamId = uiState.value.teamId ?: return
        if (!row.hasSong) {
            toastState.value = ToastState(message = "아직 개인 응원가가 없어요", isVisible = true)
            return
        }
        playTeamMembersUseCase.playSelected(row, uiState.value.rows, teamId)
    }

    fun dismissToast() {
        toastState.update { it.copy(isVisible = false) }
    }
}
