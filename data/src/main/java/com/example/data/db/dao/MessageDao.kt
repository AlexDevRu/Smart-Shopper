package com.example.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity
import com.example.data.db.relation.MessageWithProducts

@Dao
interface MessageDao {
    @Transaction
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesPaged(chatId: String): PagingSource<Int, MessageWithProducts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Transaction
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getRecentMessages(chatId: String, limit: Int): List<MessageWithProducts>
}
