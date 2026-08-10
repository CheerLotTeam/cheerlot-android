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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    getSelectedTeamUseCase: GetSelectedTeamUseCase,
    getAppIconModeUseCase: GetAppIconModeUseCase,
    private val setAppIconModeUseCase: SetAppIconModeUseCase,
    private val appIconSwitcher: AppIconSwitcher
) : ViewModel() {

    private data class ToastState(
        val message: String = "",
        val isVisible: Boolean = false
    )

    private val toastState = MutableStateFlow(ToastState())

    val uiState: StateFlow<SettingsUiState> = combine(
        getSelectedTeamUseCase(),
        getAppIconModeUseCase(),
        toastState
    ) { teamId, appIconMode, toast ->
        SettingsUiState(
            currentTeam = teamId?.let(TeamCatalog::findById),
            appIconMode = appIconMode,
            appVersion = BuildConfig.VERSION_NAME,
            toastMessage = toast.message,
            isToastVisible = toast.isVisible
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(appVersion = BuildConfig.VERSION_NAME)
        )

    // 선택 즉시 PackageManager에 반영하면 시스템이 앱을 포그라운드에서 내려버려서, 원하는
    // 상태만 기록해두고 실제 반영은 MainActivity.onPause()에서 처리합니다.
    // DataStore 쓰기(setAppIconModeUseCase)가 실패하면 아이콘 전환도 건너뛰고 토스트로 알립니다
    // — 저장이 안 됐는데 아이콘만 바뀌면 다음 실행 시 상태가 다시 어긋나기 때문입니다.
    fun onSelectAppIconMode(mode: AppIconMode) {
        val teamId = uiState.value.currentTeam?.id
        viewModelScope.launch {
            setAppIconModeUseCase(mode)
                .onSuccess { appIconSwitcher.requestSwitch(teamId, mode) }
                .onFailure {
                    toastState.value = ToastState(message = "아이콘 설정을 저장하지 못했어요", isVisible = true)
                }
        }
    }

    fun dismissToast() {
        toastState.update { it.copy(isVisible = false) }
    }
}
