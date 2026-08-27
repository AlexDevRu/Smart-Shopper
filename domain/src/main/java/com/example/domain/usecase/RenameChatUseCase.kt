package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import javax.inject.Inject

class RenameChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, newTitle: String): Result<Unit> =
        repository.renameChat(chatId, newTitle)
}
