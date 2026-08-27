package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Message
import com.example.domain.repository.ChatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMessagesUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = GetMessagesUseCase(repository)

    @Test
    fun `invoke should return flow of paging data from repository`() = runTest {
        // Given
        val chatId = "chat_123"
        val pagingData = PagingData.from(listOf<Message>())
        val flow = flowOf(pagingData)
        every { repository.getMessagesPaged(chatId) } returns flow

        // When
        val result = useCase(chatId)

        // Then
        assertEquals(pagingData, result.first())
    }
}
