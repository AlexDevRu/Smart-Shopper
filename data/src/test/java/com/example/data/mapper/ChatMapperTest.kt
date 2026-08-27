package com.example.data.mapper

import com.example.data.db.entity.ChatEntity
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity
import com.example.data.db.relation.MessageWithProducts
import com.example.domain.model.Chat
import com.example.domain.model.Message
import com.example.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ChatMapperTest {

    @Test
    fun `ChatEntity toDomain should map correctly`() {
        val now = Instant.now()
        val entity = ChatEntity(
            id = "chat_1",
            title = "Title",
            lastMessage = "Last",
            timestamp = now
        )
        val domain = entity.toDomain()

        assertEquals("chat_1", domain.id)
        assertEquals("Title", domain.title)
        assertEquals("Last", domain.lastMessage)
        assertEquals(now, domain.timestamp)
    }

    @Test
    fun `MessageWithProducts toDomain should map correctly`() {
        val now = Instant.now()
        val messageEntity = MessageEntity(
            id = "msg_1",
            chatId = "chat_1",
            text = "Hello",
            isFromUser = true,
            timestamp = now,
            wasSearchTriggered = false
        )
        val productEntity = ProductEntity(
            id = "prod_1",
            messageId = "msg_1",
            name = "Product",
            price = "$10",
            imageUrl = "url",
            rating = 4.5,
            numReviews = 10,
            url = "link"
        )
        val relation = MessageWithProducts(
            message = messageEntity,
            products = listOf(productEntity)
        )

        val domain = relation.toDomain()

        assertEquals("msg_1", domain.id)
        assertEquals("chat_1", domain.chatId)
        assertEquals("Hello", domain.text)
        assertEquals(true, domain.isFromUser)
        assertEquals(now, domain.timestamp)
        assertEquals(1, domain.products.size)
        assertEquals("prod_1", domain.products[0].id)
    }

    @Test
    fun `Message toEntity should map correctly`() {
        val now = Instant.now()
        val domain = Message(
            id = "msg_1",
            chatId = "chat_1",
            text = "Hello",
            isFromUser = true,
            timestamp = now,
            wasSearchTriggered = true
        )
        val entity = domain.toEntity()

        assertEquals("msg_1", entity.id)
        assertEquals("chat_1", entity.chatId)
        assertEquals("Hello", entity.text)
        assertEquals(true, entity.isFromUser)
        assertEquals(now, entity.timestamp)
        assertEquals(true, entity.wasSearchTriggered)
    }
}
