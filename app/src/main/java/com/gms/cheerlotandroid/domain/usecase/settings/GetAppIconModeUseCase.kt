package com.gms.cheerlotandroid.domain.usecase.settings

import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow

class GetAppIconModeUseCase(
    private val userSettingsRepository: UserSettingsRepository
) {
    operator fun invoke(): Flow<AppIconMode> = userSettingsRepository.appIconMode
}
