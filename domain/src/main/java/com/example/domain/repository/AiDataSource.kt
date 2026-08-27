package com.example.domain.repository

import com.example.domain.model.AiResult
import com.example.domain.model.Message

interface AiDataSource {
    suspend fun generateResponse(userInput: String, history: List<Message>): AiResult
}
