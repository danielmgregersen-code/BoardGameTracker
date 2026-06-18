package com.daniel.boardgametracker.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.daniel.boardgametracker.data.model.OathswornState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.oathswornDataStore: DataStore<Preferences> by preferencesDataStore(name = "oathsworn_helper")

/** Persists the entire Oathsworn helper state (Free Company + roster) as a JSON blob. */
class OathswornPrefs(private val context: Context) {
    companion object {
        private val KEY_STATE = stringPreferencesKey("oathsworn_state")
    }

    fun getState(): Flow<OathswornState> = context.oathswornDataStore.data.map { prefs ->
        prefs[KEY_STATE]?.let {
            runCatching { Json.decodeFromString<OathswornState>(it) }.getOrNull()
        } ?: OathswornState()
    }

    suspend fun saveState(state: OathswornState) {
        context.oathswornDataStore.edit { it[KEY_STATE] = Json.encodeToString(state) }
    }
}
