package com.gms.cheerlotandroid.presentation.appflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gms.cheerlotandroid.domain.service.remoteconfig.RemoteConfigService
import com.gms.cheerlotandroid.domain.usecase.team.HasSelectedTeamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppFlowViewModel(
    private val hasSelectedTeamUseCase: HasSelectedTeamUseCase,
    private val remoteConfigService: RemoteConfigService,
    private val currentVersion: String,
) : ViewModel() {
    private val _state = MutableStateFlow<AppFlowState>(AppFlowState.Splash)
    val state: StateFlow<AppFlowState> = _state.asStateFlow()

    private var isSplashFinished = false
    private var isConfigChecked = false

    init {
        fetchRemoteConfig()
    }

    fun onSplashFinished() {
        isSplashFinished = true
        movePastSplashIfReady()
    }

    fun onTeamSelected() {
        _state.value = AppFlowState.Main
    }

    private fun fetchRemoteConfig() {
        viewModelScope.launch {
            val config = remoteConfigService.fetch()
            when {
                config.isServerChecking -> {
                    _state.value = AppFlowState.ServerChecking(config.serverCheckingMessage)
                }
                isUpdateRequired(currentVersion, config.minimumVersion) -> {
                    _state.value = AppFlowState.UpdateRequired(config.minimumVersion)
                }
                else -> {
                    isConfigChecked = true
                    movePastSplashIfReady()
                }
            }
        }
    }

    private fun movePastSplashIfReady() {
        if (!isSplashFinished || !isConfigChecked) return
        viewModelScope.launch {
            _state.value = if (hasSelectedTeamUseCase().first()) {
                AppFlowState.Main
            } else {
                AppFlowState.Onboarding
            }
        }
    }

    private fun isUpdateRequired(current: String, minimum: String): Boolean {
        val currentParts = current.toVersionParts()
        val minimumParts = minimum.toVersionParts()
        val size = maxOf(currentParts.size, minimumParts.size)
        return (0 until size).firstNotNullOfOrNull { index ->
            val currentPart = currentParts.getOrElse(index) { 0 }
            val minimumPart = minimumParts.getOrElse(index) { 0 }
            when {
                currentPart < minimumPart -> true
                currentPart > minimumPart -> false
                else -> null
            }
        } ?: false
    }

    private fun String.toVersionParts(): List<Int> =
        split('.').map { part -> part.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
}
