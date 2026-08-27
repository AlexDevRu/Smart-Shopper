package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLanguage(): Flow<String>
    fun getCountry(): Flow<String>
    suspend fun setLanguage(lang: String)
    suspend fun setCountry(country: String)
}
