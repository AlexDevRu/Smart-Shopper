package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateChatUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = CreateChatUseCase(repository)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val chatId = "chat_123"
        coEvery { repository.createChat() } returns Result.success(chatId)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(chatId, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val exception = Exception("Failed to create chat")
        coEvery { repository.createChat() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
