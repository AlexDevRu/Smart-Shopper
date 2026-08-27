package com.example.domain.repository

import com.example.domain.model.Product

interface OpenWebNinjaDataSource {
    suspend fun searchProducts(
        query: String,
        maxPrice: Double?,
        language: String? = null,
        country: String? = null
    ): List<Product>
}
