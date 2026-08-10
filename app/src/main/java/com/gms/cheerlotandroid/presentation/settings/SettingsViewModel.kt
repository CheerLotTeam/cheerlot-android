package com.gms.cheerlotandroid.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.BuildConfig
import com.gms.cheerlotandroid.core.icon.AppIconSwitcher
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.usecase.settings.GetAppIconModeUseCase
import com.gms.cheerlotandroid.domain.usecase.settings.SetAppIconModeUseCase
import com.gms.cheerlotandroid.domain.usecase.team.GetSelectedTeamUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase,
    getAppIconModeUseCase: GetAppIconModeUseCase,
    private val setAppIconModeUseCase: SetAppIconModeUseCase,
    private val appIconSwitcher: AppIconSwitcher
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        getSelectedTeamUseCase(),
        getAppIconModeUseCase()
    ) { teamId, appIconMode ->
        SettingsUiState(
            currentTeam = teamId?.let(TeamCatalog::findById),
            appIconMode = appIconMode,
            appVersion = BuildConfig.VERSION_NAME
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(appVersion = BuildConfig.VERSION_NAME)
        )

    // 선택 즉시 PackageManager에 반영하면 시스템이 앱을 포그라운드에서 내려버려서, 원하는
    // 상태만 기록해두고 실제 반영은 MainActivity.onPause()에서 처리합니다.
    fun onSelectAppIconMode(mode: AppIconMode) {
        val teamId = uiState.value.currentTeam?.id
        viewModelScope.launch {
            setAppIconModeUseCase(mode)
            appIconSwitcher.requestSwitch(teamId, mode)
        }
    }
}
