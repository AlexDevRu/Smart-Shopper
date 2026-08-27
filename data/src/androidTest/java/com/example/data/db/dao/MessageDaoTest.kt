package com.example.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.db.ChatDatabase
import com.example.data.db.entity.ChatEntity
import com.example.data.db.entity.MessageEntity
import com.example.data.db.entity.ProductEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class MessageDaoTest {

    private lateinit var db: ChatDatabase
    private lateinit var chatDao: ChatDao
    private lateinit var messageDao: MessageDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ChatDatabase::class.java).build()
        chatDao = db.chatDao()
        messageDao = db.messageDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertMessageWithProductsAndRetrieve() = runTest {
        val chatId = "chat1"
        chatDao.insertChat(ChatEntity(chatId, "Title", "", Instant.now()))

        val message = MessageEntity("msg1", chatId, "Hello", true, Instant.now(), false)
        val product = ProductEntity("prod1", "msg1", "Product", "$10", "url", 4.5, 10, "link")

        messageDao.insertMessage(message)
        messageDao.insertProducts(listOf(product))

        val recent = messageDao.getRecentMessages(chatId, 10)
        assertEquals(1, recent.size)
        assertEquals("msg1", recent[0].message.id)
        assertEquals(1, recent[0].products.size)
        assertEquals("prod1", recent[0].products[0].id)
    }
}
