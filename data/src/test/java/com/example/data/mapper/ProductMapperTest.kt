package com.example.data.mapper

import com.example.data.db.entity.ProductEntity
import com.example.data.remote.ShoppingProductDto
import com.example.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `ProductEntity toDomain should map correctly`() {
        val entity = ProductEntity(
            id = "prod_1",
            messageId = "msg_1",
            name = "Name",
            price = "$10",
            imageUrl = "image",
            rating = 4.0,
            numReviews = 5,
            url = "url"
        )
        val domain = entity.toDomain()

        assertEquals("prod_1", domain.id)
        assertEquals("Name", domain.name)
        assertEquals("$10", domain.price)
        assertEquals("image", domain.imageUrl)
        assertEquals(4.0, domain.rating)
        assertEquals(5, domain.numReviews)
        assertEquals("url", domain.url)
    }

    @Test
    fun `Product toEntity should map correctly`() {
        val domain = Product(
            id = "prod_1",
            name = "Name",
            price = "$10",
            imageUrl = "image",
            rating = 4.0,
            numReviews = 5,
            url = "url"
        )
        val entity = domain.toEntity("msg_1")

        assertEquals("prod_1", entity.id)
        assertEquals("msg_1", entity.messageId)
        assertEquals("Name", entity.name)
        assertEquals("$10", entity.price)
        assertEquals("image", entity.imageUrl)
        assertEquals(4.0, entity.rating)
        assertEquals(5, entity.numReviews)
        assertEquals("url", entity.url)
    }

    @Test
    fun `ShoppingProductDto toDomain should map correctly`() {
        val dto = ShoppingProductDto(
            id = "prod_1",
            name = "Name",
            price = "$10",
            photos = listOf("photo1", "photo2"),
            rating = 4.5,
            numReviews = 100,
            url = "url"
        )
        val domain = dto.toDomain()

        assertEquals("prod_1", domain.id)
        assertEquals("Name", domain.name)
        assertEquals("$10", domain.price)
        assertEquals("photo1", domain.imageUrl)
        assertEquals(4.5, domain.rating)
        assertEquals(100, domain.numReviews)
        assertEquals("url", domain.url)
    }
}
