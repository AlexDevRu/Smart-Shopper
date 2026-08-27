package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RenameChatUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = RenameChatUseCase(repository)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val chatId = "chat_123"
        val newTitle = "New Title"
        coEvery { repository.renameChat(chatId, newTitle) } returns Result.success(Unit)

        // When
        val result = useCase(chatId, newTitle)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val chatId = "chat_123"
        val newTitle = "New Title"
        val exception = Exception("Failed to rename")
        coEvery { repository.renameChat(chatId, newTitle) } returns Result.failure(exception)

        // When
        val result = useCase(chatId, newTitle)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
