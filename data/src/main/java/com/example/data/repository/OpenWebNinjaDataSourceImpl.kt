package com.example.data.repository

import com.example.data.mapper.toDomain
import com.example.data.remote.ProductApiService
import com.example.domain.model.Product
import com.example.domain.repository.OpenWebNinjaDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWebNinjaDataSourceImpl @Inject constructor(
    private val apiService: ProductApiService
) : OpenWebNinjaDataSource {
    override suspend fun searchProducts(
        query: String,
        maxPrice: Double?,
        language: String?,
        country: String?
    ): List<Product> {
        return apiService.searchProducts(
            query = query,
            maxPrice = maxPrice,
            language = language,
            country = country
        ).data?.products.orEmpty().map { it.toDomain() }
    }
}
