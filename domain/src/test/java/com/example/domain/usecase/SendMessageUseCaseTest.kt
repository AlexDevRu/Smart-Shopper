package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SendMessageUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = SendMessageUseCase(repository)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val chatId = "chat_123"
        val text = "Hello"
        coEvery { repository.sendMessage(chatId, text) } returns Result.success(Unit)

        // When
        val result = useCase(chatId, text)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val chatId = "chat_123"
        val text = "Hello"
        val exception = Exception("Send failed")
        coEvery { repository.sendMessage(chatId, text) } returns Result.failure(exception)

        // When
        val result = useCase(chatId, text)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
