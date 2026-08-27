package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteChatUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = DeleteChatUseCase(repository)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val chatId = "chat_123"
        coEvery { repository.deleteChat(chatId) } returns Result.success(Unit)

        // When
        val result = useCase(chatId)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val chatId = "chat_123"
        val exception = Exception("Failed to delete chat")
        coEvery { repository.deleteChat(chatId) } returns Result.failure(exception)

        // When
        val result = useCase(chatId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
