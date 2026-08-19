package com.gms.cheerlotandroid.data.storage.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

const val TEAM_SELECTION_DATASTORE_NAME = "team_selection"

val Context.teamSelectionDataStore by preferencesDataStore(name = TEAM_SELECTION_DATASTORE_NAME)
