package com.example.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.db.ChatDatabase
import com.example.data.db.entity.ChatEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class ChatDaoTest {

    private lateinit var db: ChatDatabase
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ChatDatabase::class.java).build()
        chatDao = db.chatDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetChat() = runTest {
        val chat = ChatEntity("id1", "Title", "Last", Instant.now())
        chatDao.insertChat(chat)
        
        val result = chatDao.getChatById("id1")
        assertEquals(chat.id, result?.id)
        assertEquals(chat.title, result?.title)
    }

    @Test
    fun deleteChat() = runTest {
        val chat = ChatEntity("id1", "Title", "Last", Instant.now())
        chatDao.insertChat(chat)
        chatDao.deleteChatById("id1")
        
        val result = chatDao.getChatById("id1")
        assertNull(result)
    }
}
