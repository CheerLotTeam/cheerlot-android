package com.gms.cheerlotandroid.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.BuildConfig
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class SettingsViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getSelectedTeamUseCase()
        .map { teamId ->
            SettingsUiState(
                currentTeam = teamId?.let(TeamCatalog::findById),
                appVersion = BuildConfig.VERSION_NAME
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(appVersion = BuildConfig.VERSION_NAME)
        )
}
