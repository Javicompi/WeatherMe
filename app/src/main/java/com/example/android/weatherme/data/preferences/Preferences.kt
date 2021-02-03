package com.example.android.weatherme.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class Preferences(context: Context) {

    companion object {
        val SELECTED_CURRENT = longPreferencesKey("selected_current")
        val LAST_UPDATE_TIME = longPreferencesKey("last_update_time")
    }

    private val dataStore = context.createDataStore(name = "preferences")

    suspend fun storeCurrentSelected(currentSelected: Long) {
        dataStore.edit {
            it[SELECTED_CURRENT] = currentSelected
        }
    }

    suspend fun storeLastUpdateTime(lastUpdate: Long) {
        dataStore.edit {
            it[LAST_UPDATE_TIME] = lastUpdate
        }
    }

    suspend fun getCurrentSelected(): Long {
        return dataStore.data.map {
            it[SELECTED_CURRENT] ?: 0L
        }.first()
    }

    suspend fun getLastUpdateTime(): Long {
        return dataStore.data.map {
            it[LAST_UPDATE_TIME] ?: 0L
        }.first()
    }
}