package com.example.domain.model

import java.time.Instant

data class Message(
    val id: String,
    val chatId: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Instant = Instant.now(),
    val products: List<Product> = emptyList(),
    val wasSearchTriggered: Boolean = false
)
