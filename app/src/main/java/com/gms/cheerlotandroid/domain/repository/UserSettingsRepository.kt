package com.gms.cheerlotandroid.domain.repository

import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val appIconMode: Flow<AppIconMode>

    // DataStore 쓰기가 실패(IOException 등)할 수 있어 Result로 감싸서, 호출부가 실패를
    // 조용히 무시하지 않고 명시적으로 처리하도록 강제합니다.
    suspend fun setAppIconMode(mode: AppIconMode): Result<Unit>
}
