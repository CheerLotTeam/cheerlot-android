package com.gms.cheerlotandroid.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.playback.PlaybackMode
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.service.playback.AudioPlayer
import com.gms.cheerlotandroid.domain.usecase.player.GetAllPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import com.gms.cheerlotandroid.domain.usecase.team.UpdateSelectedTeamUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// [임시/테스트용] 전체선수 탭 - 정식 팀원 목록 화면이 없는 상태에서 오디오 재생 파이프라인을
// 실제 서버 데이터(원격 audioUrl)로 검증하기 위한 화면입니다. 정식 화면이 생기면 대체됩니다.
internal data class TeamMembersTestRow(
    val playerName: String,
    val song: CheerSongInfo
)

internal data class TeamMembersTestUiState(
    val teamId: TeamId? = null,
    val rows: List<TeamMembersTestRow> = emptyList(),
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class TeamMembersTestViewModel(
    private val getSelectedTeamUseCase: GetSelectedTeamUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
    private val updateSelectedTeamUseCase: UpdateSelectedTeamUseCase,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val uiState: StateFlow<TeamMembersTestUiState> = getSelectedTeamUseCase()
        .flatMapLatest { teamId ->
            if (teamId == null) {
                flowOf(TeamMembersTestUiState(teamId = null, rows = emptyList()))
            } else {
                flow { emitAll(getAllPlayersUseCase(teamId)) }
                    .map { players ->
                        val rows = players.flatMap { player ->
                            player.cheerSongs.map { song -> TeamMembersTestRow(player.name, song) }
                        }
                        TeamMembersTestUiState(teamId = teamId, rows = rows)
                    }
                    .catch { throwable ->
                        emit(
                            TeamMembersTestUiState(
                                teamId = teamId,
                                rows = emptyList(),
                                errorMessage = throwable.message ?: "선수 목록을 불러오지 못했습니다."
                            )
                        )
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamMembersTestUiState()
        )

    // [임시/테스트용] 정식 팀 선택 화면이 없는 상태에서 재생 파이프라인을 검증하기 위한 지름길입니다.
    fun selectTeamForTesting(teamId: TeamId) {
        viewModelScope.launch {
            updateSelectedTeamUseCase(teamId)
        }
    }

    fun onRowClick(index: Int) {
        val state = uiState.value
        val songs = state.rows.map { it.song }
        val playerNames = state.rows.map { it.playerName }
        if (songs.isEmpty() || index !in songs.indices) return

        audioPlayer.playQueue(
            songs = songs,
            playerNames = playerNames,
            startAt = index,
            teamId = state.teamId,
            mode = PlaybackMode.NORMAL
        )
    }
}
