package com.example.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val COUNTRY = stringPreferencesKey("country")
    }

    override fun getLanguage(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE] ?: "en"
    }

    override fun getCountry(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTRY] ?: "us"
    }

    override suspend fun setLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = lang
        }
    }

    override suspend fun setCountry(country: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTRY] = country
        }
    }
}
