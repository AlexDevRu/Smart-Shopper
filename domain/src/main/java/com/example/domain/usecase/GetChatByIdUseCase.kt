package com.example.domain.usecase

import com.example.domain.model.Chat
import com.example.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatByIdUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): Result<Chat?> = repository.getChatById(chatId)
}
