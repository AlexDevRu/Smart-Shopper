package com.example.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.db.dao.ChatDao
import com.example.data.db.dao.MessageDao
import com.example.data.db.entity.ChatEntity
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class, ProductEntity::class],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
