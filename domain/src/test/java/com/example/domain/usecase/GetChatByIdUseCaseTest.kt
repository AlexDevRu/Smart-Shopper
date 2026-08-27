package com.example.domain.usecase

import com.example.domain.model.Chat
import com.example.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetChatByIdUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = GetChatByIdUseCase(repository)

    @Test
    fun `invoke should return success with chat when repository succeeds`() = runTest {
        // Given
        val chatId = "chat_123"
        val chat = Chat(id = chatId, title = "Title", lastMessage = "Last")
        coEvery { repository.getChatById(chatId) } returns Result.success(chat)

        // When
        val result = useCase(chatId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(chat, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val chatId = "chat_123"
        val exception = Exception("Chat not found")
        coEvery { repository.getChatById(chatId) } returns Result.failure(exception)

        // When
        val result = useCase(chatId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
