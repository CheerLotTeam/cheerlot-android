package com.gms.cheerlotandroid.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.player.ObserveAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.playback.PlaySearchResultUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.IsGameDayUseCase
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow
import com.gms.cheerlotandroid.presentation.teammembers.toTeamMembersRows
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val MAX_QUERY_LENGTH = 12

private data class TeamRows(
    val teamId: TeamId?,
    val rows: List<TeamMembersRow> = emptyList(),
    val isGameDay: Boolean = false,
    val isLoading: Boolean = false
)

// iOS SearchViewModel과 동일하게, 현재 선택된 팀 로스터 안에서만 선수 이름으로 찾습니다(전체 팀 통합
// 검색 아님). 필터링은 서버/DB가 아니라 이미 로드된 로스터를 대상으로 클라이언트에서 처리합니다.
@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val observeAllPlayersUseCase: ObserveAllPlayersUseCase,
    private val isGameDayUseCase: IsGameDayUseCase,
    private val playSearchResultUseCase: PlaySearchResultUseCase
) : ViewModel() {

    private data class ToastState(
        val message: String = "",
        val isVisible: Boolean = false
    )

    private val query = MutableStateFlow("")
    private val toastState = MutableStateFlow(ToastState())

    // iOS SearchViewModel과 동일하게, 검색은 동기화(네트워크)하지 않고 이미 동기화된 로컬 로스터만
    // 관찰합니다(전체선수/라인업이 최신화해둔 데이터). 네트워크를 타지 않으므로 에러/재시도 상태가 없습니다.
    private val teamRowsState: Flow<TeamRows> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(TeamRows(teamId = null))
            } else {
                combine(
                    observeAllPlayersUseCase(teamId),
                    isGameDayUseCase(teamId),
                ) { players, isGameDay ->
                    TeamRows(
                        teamId = teamId,
                        rows = players.toTeamMembersRows(),
                        isGameDay = isGameDay,
                    )
                }
                    .onStart { emit(TeamRows(teamId = teamId, isLoading = true)) }
                    // 로컬 Room Flow라 사실상 실패하지 않지만, 예외로 수집이 끊기지 않도록 빈 목록으로 폴백합니다.
                    .catch { emit(TeamRows(teamId = teamId)) }
            }
        }

    val uiState: StateFlow<SearchUiState> =
        combine(teamRowsState, query, toastState) { teamRows, currentQuery, toast ->
            SearchUiState(
                teamId = teamRows.teamId,
                query = currentQuery,
                results = if (currentQuery.isBlank()) {
                    emptyList()
                } else {
                    teamRows.rows.filter { it.playerName.contains(currentQuery) }
                },
                isGameDay = teamRows.isGameDay,
                isLoading = teamRows.isLoading,
                toastMessage = toast.message,
                isToastVisible = toast.isVisible
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun onQueryChange(newQuery: String) {
        query.value = newQuery.take(MAX_QUERY_LENGTH)
    }

    fun onTapResult(row: TeamMembersRow) {
        val teamId = uiState.value.teamId ?: return
        if (!row.hasSong) {
            toastState.value = ToastState(message = "아직 개인 응원가가 없어요", isVisible = true)
            return
        }
        playSearchResultUseCase.play(
            row,
            uiState.value.results,
            teamId,
            uiState.value.isGameDay,
        )
    }

    fun dismissToast() {
        toastState.update { it.copy(isVisible = false) }
    }
}
