package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Chat
import com.example.domain.repository.ChatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetChatsUseCaseTest {

    private val repository: ChatRepository = mockk()
    private val useCase = GetChatsUseCase(repository)

    @Test
    fun `invoke should return flow of paging data from repository`() = runTest {
        // Given
        val pagingData = PagingData.from(listOf<Chat>())
        val flow = flowOf(pagingData)
        every { repository.getChatsPaged() } returns flow

        // When
        val result = useCase()

        // Then
        assertEquals(pagingData, result.first())
    }
}
