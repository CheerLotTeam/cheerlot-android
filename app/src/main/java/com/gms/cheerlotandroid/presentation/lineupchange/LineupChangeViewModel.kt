package com.gms.cheerlotandroid.presentation.lineupchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.usecase.lineup.GetBenchPlayersUseCase
import com.gms.cheerlotandroid.domain.usecase.lineup.SwapLineupPlayersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LineupChangeViewModel(
    private val getBenchPlayersUseCase: GetBenchPlayersUseCase,
    private val swapLineupPlayersUseCase: SwapLineupPlayersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LineupChangeUiState())
    val uiState: StateFlow<LineupChangeUiState> = _uiState.asStateFlow()
    private var lineupMember: PlayerInfo? = null
    private var benchPlayersJob: Job? = null
    private var swapJob: Job? = null

    fun initialize(lineupMember: PlayerInfo) {
        this.lineupMember = lineupMember
        benchPlayersJob?.cancel()
        swapJob?.cancel()
        _uiState.value = LineupChangeUiState(lineupMember = lineupMember)
        observeBenchPlayers(lineupMember)
    }

    fun selectMember(member: PlayerInfo) {
        if (_uiState.value.isSwapping) return
        _uiState.update { state ->
            state.copy(
                selectedMemberId = if (state.selectedMemberId == member.id) null else member.id
            )
        }
    }

    fun swapPlayers(onSuccess: () -> Unit) {
        if (_uiState.value.isSwapping) return
        val lineupMember = lineupMember ?: return
        val selectedMember = selectedMember() ?: run {
            _uiState.update {
                it.copy(
                    toastMessage = "교체할 선수를 선택해 주세요",
                    isToastVisible = true
                )
            }
            return
        }

        swapJob = viewModelScope.launch {
            _uiState.update { it.copy(isSwapping = true, errorMessage = null) }
            runCatching {
                swapLineupPlayersUseCase(lineupMember, selectedMember)
            }.onSuccess {
                _uiState.update { it.copy(isSwapping = false) }
                onSuccess()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSwapping = false,
                        errorMessage = "선수를 교체하지 못했습니다."
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(isToastVisible = false) }
    }

    fun resetTransientState() {
        benchPlayersJob?.cancel()
        benchPlayersJob = null
        swapJob?.cancel()
        swapJob = null
        lineupMember = null
        _uiState.value = LineupChangeUiState()
    }

    private fun observeBenchPlayers(lineupMember: PlayerInfo) {
        benchPlayersJob = viewModelScope.launch {
            getBenchPlayersUseCase(lineupMember.teamId)
                .catch {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "교체 선수를 불러오지 못했습니다."
                        )
                    }
                }
                .collect { benchMembers ->
                    _uiState.update { state ->
                        state.copy(
                            benchMembers = benchMembers,
                            selectedMemberId = state.selectedMemberId
                                ?.takeIf { selectedId -> benchMembers.any { it.id == selectedId } },
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun selectedMember(): PlayerInfo? {
        val selectedMemberId: PlayerId = _uiState.value.selectedMemberId ?: return null
        return _uiState.value.benchMembers.firstOrNull { it.id == selectedMemberId }
    }
}
