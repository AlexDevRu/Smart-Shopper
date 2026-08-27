package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): Result<Unit> = repository.deleteChat(chatId)
}
