package com.example.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity

data class MessageWithProducts(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId"
    )
    val products: List<ProductEntity>
)
