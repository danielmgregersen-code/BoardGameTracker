package com.daniel.boardgametracker.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "last_sessions")

class LastSessionPrefs(private val context: Context) {
    companion object {
        private val KEY_VOIDFALL = stringPreferencesKey("last_voidfall")
        private val KEY_FINAL_GIRL = stringPreferencesKey("last_final_girl")
        private val KEY_SPIRIT_ISLAND = stringPreferencesKey("last_spirit_island")
    }

    fun getLastVoidfall(): Flow<String?> = context.dataStore.data.map { it[KEY_VOIDFALL] }
    fun getLastFinalGirl(): Flow<String?> = context.dataStore.data.map { it[KEY_FINAL_GIRL] }
    fun getLastSpiritIsland(): Flow<String?> = context.dataStore.data.map { it[KEY_SPIRIT_ISLAND] }

    suspend fun saveLastVoidfall(json: String) { context.dataStore.edit { it[KEY_VOIDFALL] = json } }
    suspend fun saveLastFinalGirl(json: String) { context.dataStore.edit { it[KEY_FINAL_GIRL] = json } }
    suspend fun saveLastSpiritIsland(json: String) { context.dataStore.edit { it[KEY_SPIRIT_ISLAND] = json } }
}
