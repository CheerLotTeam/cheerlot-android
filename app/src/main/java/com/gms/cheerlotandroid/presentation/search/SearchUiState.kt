package com.gms.cheerlotandroid.presentation.search

import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

internal data class SearchUiState(
    val teamId: TeamId? = null,
    val query: String = "",
    val results: List<TeamMembersRow> = emptyList(),
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
) {
    val totalSongCount: Int get() = results.count { it.hasSong }
}
