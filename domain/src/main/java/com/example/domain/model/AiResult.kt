package com.example.domain.model

/**
 * Result from AI manager containing the text response and extracted metadata.
 */
data class AiResult(
    val text: String,
    val extractedIntent: AiIntent? = null
)
