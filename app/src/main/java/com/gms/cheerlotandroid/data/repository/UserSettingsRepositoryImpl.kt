package com.gms.cheerlotandroid.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gms.cheerlotandroid.domain.model.settings.AppIconMode
import com.gms.cheerlotandroid.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val APP_ICON_MODE_KEY = stringPreferencesKey("app_icon_mode")

class UserSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {

    override val appIconMode: Flow<AppIconMode> = dataStore.data.map { preferences ->
        preferences[APP_ICON_MODE_KEY]
            ?.let { raw -> runCatching { AppIconMode.valueOf(raw) }.getOrNull() }
            ?: AppIconMode.BASE
    }

    override suspend fun setAppIconMode(mode: AppIconMode): Result<Unit> {
        return runCatching {
            dataStore.edit { preferences -> preferences[APP_ICON_MODE_KEY] = mode.name }
        }.map {}
    }
}
