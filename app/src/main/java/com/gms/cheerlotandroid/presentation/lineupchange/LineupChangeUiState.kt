package com.gms.cheerlotandroid.presentation.lineupchange

import androidx.compose.runtime.Immutable
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo

@Immutable
internal data class LineupChangeUiState(
    val lineupMember: PlayerInfo? = null,
    val benchMembers: List<PlayerInfo> = emptyList(),
    val selectedMemberId: PlayerId? = null,
    val isLoading: Boolean = true,
    val isSwapping: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String = "",
    val isToastVisible: Boolean = false
)
