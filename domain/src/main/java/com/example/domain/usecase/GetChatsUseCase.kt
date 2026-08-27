package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Chat
import com.example.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<PagingData<Chat>> = repository.getChatsPaged()
}
