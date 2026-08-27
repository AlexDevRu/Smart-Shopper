package com.example.smartshopper.di

import com.example.data.di.RepositoryModule
import com.example.data.provider.SystemSettingsProviderImpl
import com.example.data.repository.ChatRepositoryImpl
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.model.AiResult
import com.example.domain.model.Message
import com.example.domain.model.Product
import com.example.domain.provider.SystemSettingsProvider
import com.example.domain.repository.AiDataSource
import com.example.domain.repository.ChatRepository
import com.example.domain.repository.OpenWebNinjaDataSource
import com.example.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Singleton

class FakeAiDataSource @Inject constructor() : AiDataSource {
    override suspend fun generateResponse(userInput: String, history: List<Message>): AiResult {
        return AiResult("AI response for: $userInput")
    }
}

class FakeOpenWebNinjaDataSource @Inject constructor() : OpenWebNinjaDataSource {
    override suspend fun searchProducts(
        query: String,
        maxPrice: Double?,
        language: String?,
        country: String?
    ): List<Product> = emptyList()
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindAiDataSource(impl: FakeAiDataSource): AiDataSource

    @Binds
    @Singleton
    abstract fun bindShoppingDataSource(impl: FakeOpenWebNinjaDataSource): OpenWebNinjaDataSource

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSystemSettingsProvider(impl: SystemSettingsProviderImpl): SystemSettingsProvider
}
