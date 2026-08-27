package com.example.smartshopper.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.domain.usecase.CreateChatUseCase
import com.example.domain.usecase.DeleteChatUseCase
import com.example.domain.usecase.GetChatByIdUseCase
import com.example.domain.usecase.GetChatsUseCase
import com.example.domain.usecase.GetMessagesUseCase
import com.example.domain.usecase.RenameChatUseCase
import com.example.domain.usecase.SendMessageUseCase
import com.example.smartshopper.ui.chat.ChatUiIntent
import com.example.smartshopper.ui.chat.ChatViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatFlowIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var getMessagesUseCase: GetMessagesUseCase
    @Inject
    lateinit var getChatByIdUseCase: GetChatByIdUseCase
    @Inject
    lateinit var sendMessageUseCase: SendMessageUseCase
    @Inject
    lateinit var createChatUseCase: CreateChatUseCase
    @Inject
    lateinit var deleteChatUseCase: DeleteChatUseCase
    @Inject
    lateinit var renameChatUseCase: RenameChatUseCase
    @Inject
    lateinit var getChatsUseCase: GetChatsUseCase

    private lateinit var viewModel: ChatViewModel

    @Before
    fun init() {
        hiltRule.inject()
        viewModel = ChatViewModel(
            getMessagesUseCase,
            getChatByIdUseCase,
            sendMessageUseCase,
            createChatUseCase,
            deleteChatUseCase,
            renameChatUseCase,
            getChatsUseCase
        )
    }

    @Test
    fun testSendMessageFlow() = runTest {
        viewModel.uiState.test {
            // Initial state
            var state = awaitItem()
            assertEquals("", state.inputText)

            // Input text
            viewModel.handleIntent(ChatUiIntent.OnInputChanged("Buy a bike"))
            state = awaitItem()
            assertEquals("Buy a bike", state.inputText)
            assertTrue(state.isSendEnabled)

            // Send message
            viewModel.handleIntent(ChatUiIntent.OnSendClicked)
            
            // Should clear input and show loading
            state = awaitItem()
            assertEquals("", state.inputText)
            assertTrue(state.isLoading)

            // Eventually should finish loading and have a selectedChatId
            // (We might get multiple state updates as database and AI respond)
            while (state.isLoading) {
                state = awaitItem()
            }
            
            assertNotNull(state.selectedChatId)
            
            // Check messages - PagingData is hard to inspect directly without a collector
            // But we can verify the state transitions occurred.
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
