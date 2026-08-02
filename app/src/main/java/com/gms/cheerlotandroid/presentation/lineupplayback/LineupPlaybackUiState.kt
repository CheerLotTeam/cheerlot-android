package com.gms.cheerlotandroid.presentation.lineupplayback

import androidx.compose.runtime.Immutable

@Immutable
internal data class LineupPlaybackItem(
    val id: String,
    val battingOrder: Int,
    val memberName: String,
    val cheerSongTitle: String,
    val lyrics: String
)

@Immutable
internal data class LineupPlaybackUiState(
    val gameDate: String = "",
    val teamsText: String = "",
    val items: List<LineupPlaybackItem> = emptyList(),
    val startIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true
)
