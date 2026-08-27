package com.example.data.repository

import com.example.data.remote.ProductApiService
import com.example.data.remote.ShoppingProductDto
import com.example.data.remote.ShoppingSearchResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OpenWebNinjaDataSourceImplTest {

    private val apiService: ProductApiService = mockk()
    private val dataSource = OpenWebNinjaDataSourceImpl(apiService)

    @Test
    fun `searchProducts should return list of products on success`() = runTest {
        // Given
        val query = "laptop"
        val dto = ShoppingProductDto(
            id = "p1",
            name = "Laptop",
            price = "$1000",
            photos = listOf("image_url")
        )
        val response = ShoppingSearchResponseDto(
            data = ShoppingSearchResponseDto.ShoppingSearchResponseDataDto(
                products = listOf(dto)
            )
        )
        coEvery {
            apiService.searchProducts(query, any(), any(), any())
        } returns response

        // When
        val result = dataSource.searchProducts(query, null, null, null)

        // Then
        assertEquals(1, result.size)
        assertEquals("p1", result[0].id)
        assertEquals("Laptop", result[0].name)
    }

    @Test
    fun `searchProducts should return empty list when data is null`() = runTest {
        // Given
        val response = ShoppingSearchResponseDto(data = null)
        coEvery {
            apiService.searchProducts(any(), any(), any(), any())
        } returns response

        // When
        val result = dataSource.searchProducts("query", null, null, null)

        // Then
        assertEquals(0, result.size)
    }
}
