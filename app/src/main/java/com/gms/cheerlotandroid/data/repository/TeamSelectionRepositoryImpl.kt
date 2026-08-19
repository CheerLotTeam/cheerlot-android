package com.gms.cheerlotandroid.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val SELECTED_TEAM_ID_KEY = stringPreferencesKey("selected_team_id")

class TeamSelectionRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : TeamSelectionRepository {

    // DataStore가 자체적으로 변경을 감지해 Flow로 흘려보내므로 별도 브로드캐스트가 필요 없습니다.
    override val selectedTeamId: Flow<TeamId?> =
        dataStore.data.map { preferences -> preferences[SELECTED_TEAM_ID_KEY]?.let(::TeamId) }

    override suspend fun setSelectedTeamId(teamId: TeamId) {
        dataStore.edit { preferences -> preferences[SELECTED_TEAM_ID_KEY] = teamId.value }
    }
}
