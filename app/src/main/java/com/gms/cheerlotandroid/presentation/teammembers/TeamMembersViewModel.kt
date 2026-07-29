package com.gms.cheerlotandroid.presentation.teammembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
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

internal data class TeamMembersUiState(
    val teamId: TeamId? = null,
    val rows: List<TeamMembersRowVO> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null
) {
    val totalSongCount: Int get() = rows.count { it.hasSong }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class TeamMembersViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
    private val playTeamMembersUseCase: PlayTeamMembersUseCase
) : ViewModel() {

    private val refreshCount = MutableStateFlow(0)
    private val snackbarMessage = MutableStateFlow<String?>(null)

    private val contentState = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(TeamMembersUiState())
            } else {
                refreshCount.flatMapLatest { refreshIndex ->
                    flow { emitAll(getAllPlayersUseCase(teamId)) }
                        .map { players -> TeamMembersUiState(teamId = teamId, rows = buildRows(players)) }
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

    val uiState: StateFlow<TeamMembersUiState> = combine(contentState, snackbarMessage) { state, message ->
        state.copy(snackbarMessage = message)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamMembersUiState())

    fun refresh() {
        refreshCount.value += 1
    }

    fun onTapPlayAll() {
        val state = uiState.value
        state.teamId?.let { playTeamMembersUseCase.playAll(state.rows, it) }
    }

    fun onTapSong(row: TeamMembersRowVO) {
        val teamId = uiState.value.teamId ?: return
        if (!row.hasSong) {
            snackbarMessage.value = "아직 개인 응원가가 없어요"
            return
        }
        playTeamMembersUseCase.playSelected(row, uiState.value.rows, teamId)
    }

    fun onSnackbarShown() {
        snackbarMessage.value = null
    }
}

// 응원가 있는 선수 먼저, 그다음 이름순. 선수당 응원가가 여러 개면 곡 개수만큼 row로 펼칩니다(iOS TeamMembersViewModel과 동일).
private fun buildRows(players: List<PlayerInfo>): List<TeamMembersRowVO> {
    return players
        .sortedWith(compareByDescending<PlayerInfo> { it.cheerSongs.isNotEmpty() }.thenBy { it.name })
        .flatMap { player ->
            if (player.cheerSongs.isEmpty()) {
                listOf(TeamMembersRowVO(id = "${player.id.value}-empty", playerName = player.name, backNumber = player.backNumber, song = null))
            } else {
                player.cheerSongs.map { song ->
                    TeamMembersRowVO(id = "${player.id.value}-${song.id}", playerName = player.name, backNumber = player.backNumber, song = song)
                }
            }
        }
}
