package com.example.submissionstoryapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DarkThemePreferences private constructor(private val dataStore: DataStore<Preferences>)  {
    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(darkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = darkTheme
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DarkThemePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): DarkThemePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = DarkThemePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}