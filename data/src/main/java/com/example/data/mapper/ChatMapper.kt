package com.example.data.mapper

import com.example.data.db.entity.ChatEntity
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity
import com.example.data.db.relation.MessageWithProducts
import com.example.domain.model.Chat
import com.example.domain.model.Message
import com.example.domain.model.Product

fun ChatEntity.toDomain() = Chat(
    id = id,
    title = title,
    lastMessage = lastMessage,
    timestamp = timestamp
)

fun MessageWithProducts.toDomain() = Message(
    id = message.id,
    chatId = message.chatId,
    text = message.text,
    isFromUser = message.isFromUser,
    timestamp = message.timestamp,
    products = products.map { it.toDomain() },
    wasSearchTriggered = message.wasSearchTriggered
)

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    rating = rating,
    numReviews = numReviews,
    url = url
)

fun Message.toEntity() = MessageEntity(
    id = id,
    chatId = chatId,
    text = text,
    isFromUser = isFromUser,
    timestamp = timestamp,
    wasSearchTriggered = wasSearchTriggered
)

fun Product.toEntity(messageId: String) = ProductEntity(
    id = id,
    messageId = messageId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    rating = rating,
    numReviews = numReviews,
    url = url
)
