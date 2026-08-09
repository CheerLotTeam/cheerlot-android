package com.gms.cheerlotandroid.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.usecase.player.GetAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.playback.PlaySearchResultUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow
import com.gms.cheerlotandroid.presentation.teammembers.toTeamMembersRows
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

private const val MAX_QUERY_LENGTH = 12

private data class TeamRows(
    val teamId: TeamId?,
    val rows: List<TeamMembersRow> = emptyList(),
    val isLoading: Boolean = false
)

// iOS SearchViewModel과 동일하게, 현재 선택된 팀 로스터 안에서만 선수 이름으로 찾습니다(전체 팀 통합
// 검색 아님). 필터링은 서버/DB가 아니라 이미 로드된 로스터를 대상으로 클라이언트에서 처리합니다.
@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
    private val playSearchResultUseCase: PlaySearchResultUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val snackbarMessage = MutableStateFlow<String?>(null)

    private val teamRowsState: Flow<TeamRows> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(TeamRows(teamId = null))
            } else {
                flow { emitAll(getAllPlayersUseCase(teamId)) }
                    .map { players -> TeamRows(teamId = teamId, rows = players.toTeamMembersRows()) }
                    .onStart { emit(TeamRows(teamId = teamId, isLoading = true)) }
            }
        }

    val uiState: StateFlow<SearchUiState> =
        combine(teamRowsState, query, snackbarMessage) { teamRows, currentQuery, message ->
            SearchUiState(
                teamId = teamRows.teamId,
                query = currentQuery,
                results = if (currentQuery.isBlank()) {
                    emptyList()
                } else {
                    teamRows.rows.filter { it.playerName.contains(currentQuery) }
                },
                isLoading = teamRows.isLoading,
                snackbarMessage = message
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun onQueryChange(newQuery: String) {
        query.value = newQuery.take(MAX_QUERY_LENGTH)
    }

    fun onTapResult(row: TeamMembersRow) {
        val teamId = uiState.value.teamId ?: return
        if (!row.hasSong) {
            snackbarMessage.value = "아직 개인 응원가가 없어요"
            return
        }
        playSearchResultUseCase.play(row, uiState.value.results, teamId)
    }

    fun onSnackbarShown() {
        snackbarMessage.value = null
    }
}
