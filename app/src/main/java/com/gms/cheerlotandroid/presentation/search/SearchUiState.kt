package com.gms.cheerlotandroid.presentation.search

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

internal data class SearchUiState(
    val teamId: TeamId? = null,
    val query: String = "",
    val results: List<TeamMembersRow> = emptyList(),
    val isGameDay: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String = "",
    val isToastVisible: Boolean = false
) {
    val totalSongCount: Int get() = results.count { it.hasSong }
}
