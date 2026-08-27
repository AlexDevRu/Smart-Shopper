package com.example.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String,
    val rating: Double? = null,
    val numReviews: Int? = null,
    val url: String
)
