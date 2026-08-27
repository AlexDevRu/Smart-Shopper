package com.example.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("shopping/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("max_price") maxPrice: Double?,
        @Query("language") language: String?,
        @Query("country") country: String?,
        @Query("return_filters") returnFilters: Boolean = false,
    ): ShoppingSearchResponseDto
}
