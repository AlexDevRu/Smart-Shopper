package com.example.domain.usecase

import com.example.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, text: String) = repository.sendMessage(chatId, text)
}
