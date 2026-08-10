package com.gms.cheerlotandroid.presentation.settings

import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

internal data class SettingsUiState(
    val currentTeam: TeamInfo? = null,
    val appIconMode: AppIconMode = AppIconMode.BASE,
    val appVersion: String = ""
)
