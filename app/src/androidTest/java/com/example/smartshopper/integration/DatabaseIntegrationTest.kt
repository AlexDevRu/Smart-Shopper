package com.example.smartshopper.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.model.Product
import com.example.domain.repository.ChatRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ChatRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testChatCreationAndMessagePersistence() = runTest {
        // Create chat
        val chatId = repository.createChat().getOrThrow()
        
        // Send message (this will use fakes for AI/Search but real DB)
        repository.sendMessage(chatId, "Hello World").getOrThrow()
        
        // Verify chat exists
        val chat = repository.getChatById(chatId).getOrThrow()
        assertEquals(chatId, chat?.id)
        assertEquals("Hello World", chat?.lastMessage)
    }
}
