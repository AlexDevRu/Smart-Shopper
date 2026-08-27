package com.example.domain.model

import java.time.Instant

data class Chat(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Instant = Instant.now()
)
