package com.example.data.di

import com.example.data.provider.SystemSettingsProviderImpl
import com.example.data.repository.AiDataSourceImpl
import com.example.data.repository.ChatRepositoryImpl
import com.example.data.repository.OpenWebNinjaDataSourceImpl
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.provider.SystemSettingsProvider
import com.example.domain.repository.AiDataSource
import com.example.domain.repository.ChatRepository
import com.example.domain.repository.OpenWebNinjaDataSource
import com.example.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindAiDataSource(
        aiDataSourceImpl: AiDataSourceImpl
    ): AiDataSource

    @Binds
    @Singleton
    abstract fun bindShoppingDataSource(
        shoppingDataSourceImpl: OpenWebNinjaDataSourceImpl
    ): OpenWebNinjaDataSource

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSystemSettingsProvider(
        systemSettingsProviderImpl: SystemSettingsProviderImpl
    ): SystemSettingsProvider
}
