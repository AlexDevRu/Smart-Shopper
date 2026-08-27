package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.data.db.ChatDatabase
import com.example.data.db.dao.ChatDao
import com.example.data.db.dao.MessageDao
import com.example.data.db.entity.ChatEntity
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.common.runCatchingCancellable
import com.example.domain.model.AiIntent
import com.example.domain.model.Chat
import com.example.domain.model.Message
import com.example.domain.model.Product
import com.example.domain.provider.SystemSettingsProvider
import com.example.domain.repository.AiDataSource
import com.example.domain.repository.ChatRepository
import com.example.domain.repository.OpenWebNinjaDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val database: ChatDatabase,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val aiDataSource: AiDataSource,
    private val shoppingDataSource: OpenWebNinjaDataSource,
    private val systemSettingsProvider: SystemSettingsProvider
) : ChatRepository {

    override fun getChatsPaged(): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { chatDao.getChatsPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getMessagesPaged(chatId: String): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { messageDao.getMessagesPaged(chatId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getChatById(chatId: String): Result<Chat?> = runCatchingCancellable {
        chatDao.getChatById(chatId)?.toDomain()
    }

    override suspend fun createChat(): Result<String> = runCatchingCancellable {
        val chatId = UUID.randomUUID().toString()
        chatDao.insertChat(
            ChatEntity(
                id = chatId,
                title = "",
                lastMessage = "",
                timestamp = Instant.now()
            )
        )
        chatId
    }

    override suspend fun deleteChat(chatId: String): Result<Unit> = runCatchingCancellable {
        chatDao.deleteChatById(chatId)
    }

    override suspend fun renameChat(chatId: String, newTitle: String): Result<Unit> = runCatchingCancellable {
        val chat = chatDao.getChatById(chatId)
        if (chat != null) {
            chatDao.updateChat(chat.copy(title = newTitle))
        }
    }

    private suspend fun saveMessage(message: Message) {
        val messageEntity = message.toEntity()
        val productEntities = message.products.map { it.toEntity(messageEntity.id) }

        database.withTransaction {
            messageDao.insertMessage(messageEntity)
            messageDao.insertProducts(productEntities)
            updateChatMetadata(message.chatId, message.text)
        }
    }

    private suspend fun updateChatMetadata(chatId: String, lastMessage: String) {
        val chat = chatDao.getChatById(chatId)
        if (chat != null) {
            chatDao.updateChat(
                chat.copy(
                    lastMessage = lastMessage,
                    timestamp = Instant.now()
                )
            )
        }
    }

    private suspend fun getRecentMessages(chatId: String, limit: Int): List<Message> {
        return messageDao.getRecentMessages(chatId, limit).map { it.toDomain() }
    }

    override suspend fun sendMessage(chatId: String, text: String): Result<Unit> = runCatchingCancellable {
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = text,
            isFromUser = true
        )
        saveMessage(userMessage)

        val history = getRecentMessages(chatId, limit = 20)

        val aiResult = aiDataSource.generateResponse(text, history)

        var products = emptyList<Product>()

        val intent = aiResult.extractedIntent
        if (intent is AiIntent.ShoppingIntent) {
            val language = systemSettingsProvider.getLanguageCode()
            val country = systemSettingsProvider.getCountryCode()

            products = shoppingDataSource.searchProducts(
                query = intent.productName,
                maxPrice = intent.price?.value,
                language = language,
                country = country
            )
        }

        val aiMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = aiResult.text,
            isFromUser = false,
            products = products,
            wasSearchTriggered = aiResult.extractedIntent is AiIntent.ShoppingIntent && products.isEmpty()
        )
        saveMessage(aiMessage)
    }
}
