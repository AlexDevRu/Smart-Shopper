package com.example.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingProductDto(
    @SerialName("product_id") val id: String,
    @SerialName("product_title") val name: String,
    @SerialName("price") val price: String,
    @SerialName("product_photos") val photos: List<String>,
    @SerialName("product_rating") val rating: Double? = null,
    @SerialName("product_num_reviews") val numReviews: Int? = null,
    @SerialName("product_page_url") val url: String = "",
)

@Serializable
data class ShoppingSearchResponseDto(
    @SerialName("data") val data: ShoppingSearchResponseDataDto?
) {
    @Serializable
    data class ShoppingSearchResponseDataDto(
        @SerialName("products") val products: List<ShoppingProductDto>
    )
}
