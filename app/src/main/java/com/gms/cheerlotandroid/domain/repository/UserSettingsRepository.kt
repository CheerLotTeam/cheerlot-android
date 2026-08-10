package com.gms.cheerlotandroid.domain.repository

import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val appIconMode: Flow<AppIconMode>
    suspend fun setAppIconMode(mode: AppIconMode)
}
