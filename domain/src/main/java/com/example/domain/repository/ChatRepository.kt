package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Chat
import com.example.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatsPaged(): Flow<PagingData<Chat>>
    fun getMessagesPaged(chatId: String): Flow<PagingData<Message>>
    suspend fun getChatById(chatId: String): Result<Chat?>
    suspend fun createChat(): Result<String>
    suspend fun deleteChat(chatId: String): Result<Unit>
    suspend fun renameChat(chatId: String, newTitle: String): Result<Unit>
    suspend fun sendMessage(chatId: String, text: String): Result<Unit>
}
