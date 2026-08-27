package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Message
import com.example.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<PagingData<Message>> = repository.getMessagesPaged(chatId)
}
