package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.db.ChatDatabase
import com.example.data.db.dao.ChatDao
import com.example.data.db.dao.MessageDao
import com.example.data.db.entity.ChatEntity
import com.example.domain.model.AiIntent
import com.example.domain.model.AiResult
import com.example.domain.provider.SystemSettingsProvider
import com.example.domain.repository.AiDataSource
import com.example.domain.repository.OpenWebNinjaDataSource
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ChatRepositoryImplTest {

    private val database: ChatDatabase = mockk()
    private val chatDao: ChatDao = mockk()
    private val messageDao: MessageDao = mockk()
    private val aiDataSource: AiDataSource = mockk()
    private val shoppingDataSource: OpenWebNinjaDataSource = mockk()
    private val systemSettingsProvider: SystemSettingsProvider = mockk()

    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setup() {
        repository = ChatRepositoryImpl(
            database,
            chatDao,
            messageDao,
            aiDataSource,
            shoppingDataSource,
            systemSettingsProvider
        )

        // Mock withTransaction to just execute the block
        mockkStatic("androidx.room.RoomDatabaseKt")
        val transactionBlock = slot<suspend () -> Any>()
        coEvery { database.withTransaction(capture(transactionBlock)) } coAnswers {
            transactionBlock.captured.invoke()
        }
    }

    @Test
    fun `sendMessage should save user message and AI response`() = runTest {
        // Given
        val chatId = "chat_1"
        val userText = "Find a laptop"
        val aiText = "Here are some laptops"
        val aiResult = AiResult(text = aiText, extractedIntent = AiIntent.ShoppingIntent("laptop", null))
        
        val chatEntity = ChatEntity(chatId, "Title", "", Instant.now())
        
        coEvery { chatDao.getChatById(chatId) } returns chatEntity
        coEvery { messageDao.insertMessage(any()) } just Runs
        coEvery { messageDao.insertProducts(any()) } just Runs
        coEvery { chatDao.updateChat(any()) } just Runs
        coEvery { messageDao.getRecentMessages(chatId, 20) } returns emptyList()
        coEvery { aiDataSource.generateResponse(userText, any()) } returns aiResult
        coEvery { systemSettingsProvider.getLanguageCode() } returns "en"
        coEvery { systemSettingsProvider.getCountryCode() } returns "US"
        coEvery { shoppingDataSource.searchProducts("laptop", null, "en", "US") } returns emptyList()

        // When
        val result = repository.sendMessage(chatId, userText)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 2) { messageDao.insertMessage(any()) } // User + AI
        coVerify { aiDataSource.generateResponse(userText, any()) }
        coVerify { shoppingDataSource.searchProducts("laptop", any(), any(), any()) }
    }

    @Test
    fun `createChat should insert new chat and return its ID`() = runTest {
        // Given
        coEvery { chatDao.insertChat(any()) } just Runs

        // When
        val result = repository.createChat()

        // Then
        assertTrue(result.isSuccess)
        val chatId = result.getOrNull()
        assertTrue(!chatId.isNullOrEmpty())
        coVerify { chatDao.insertChat(match { it.id == chatId }) }
    }
}
