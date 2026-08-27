package com.example.data.provider

import com.example.domain.provider.SystemSettingsProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemSettingsProviderImpl @Inject constructor() : SystemSettingsProvider {
    override fun getLanguageCode(): String {
        return Locale.getDefault().language
    }

    override fun getCountryCode(): String {
        return Locale.getDefault().country
    }
}
