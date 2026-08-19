package com.gms.cheerlotandroid.data.storage.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

const val USER_SETTINGS_DATASTORE_NAME = "user_settings"

val Context.userSettingsDataStore by preferencesDataStore(name = USER_SETTINGS_DATASTORE_NAME)
