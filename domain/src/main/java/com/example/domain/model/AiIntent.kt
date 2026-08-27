package com.example.domain.model

sealed interface AiIntent {
    data class ShoppingIntent(
        val productName: String,
        val price: Price?,
    ) : AiIntent
}

