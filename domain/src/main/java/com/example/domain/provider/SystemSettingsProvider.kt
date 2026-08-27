package com.example.domain.provider

interface SystemSettingsProvider {
    fun getLanguageCode(): String
    fun getCountryCode(): String
}
