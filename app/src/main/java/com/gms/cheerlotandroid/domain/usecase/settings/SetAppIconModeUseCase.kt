package com.gms.cheerlotandroid.domain.usecase.settings

import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.repository.UserSettingsRepository

class SetAppIconModeUseCase(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(mode: AppIconMode): Result<Unit> = userSettingsRepository.setAppIconMode(mode)
}
