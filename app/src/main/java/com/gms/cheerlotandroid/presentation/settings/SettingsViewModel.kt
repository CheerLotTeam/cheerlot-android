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

    // iOS SettingViewModel.didSelectAppIconMode와 동일하게, 저장과 동시에 즉시 아이콘을
    // 반영합니다(MainViewModel의 반응형 동기화만 믿으면 설정 화면을 나가야 반영되는 지연이 생김).
    fun onSelectAppIconMode(mode: AppIconMode) {
        val teamId = uiState.value.currentTeam?.id
        viewModelScope.launch {
            setAppIconModeUseCase(mode)
            appIconSwitcher.switchTo(teamId, mode)
        }
    }
}
