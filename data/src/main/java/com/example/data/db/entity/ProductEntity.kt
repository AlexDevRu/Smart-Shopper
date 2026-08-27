package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["messageId"])]
)
data class ProductEntity(
    @PrimaryKey val id: String,
    val messageId: String,
    val name: String,
    val price: String,
    val imageUrl: String,
    val rating: Double?,
    val numReviews: Int?,
    val url: String
)
