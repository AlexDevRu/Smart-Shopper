package com.example.data.mapper

import com.example.data.remote.ShoppingProductDto
import com.example.domain.model.Product

fun ShoppingProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = photos.firstOrNull() ?: "",
    rating = rating,
    numReviews = numReviews,
    url = url
)
