package com.example.smartshopper.ui.chat

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.domain.usecase.CreateChatUseCase
import com.example.domain.usecase.DeleteChatUseCase
import com.example.domain.usecase.GetChatByIdUseCase
import com.example.domain.usecase.GetChatsUseCase
import com.example.domain.usecase.GetMessagesUseCase
import com.example.domain.usecase.RenameChatUseCase
import com.example.domain.usecase.SendMessageUseCase
import com.example.smartshopper.ui.common.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val renameChatUseCase: RenameChatUseCase,
    getChatsUseCase: GetChatsUseCase
) : MviViewModel<ChatUiState, ChatUiIntent, ChatUiEffect>(ChatUiState()) {

    init {
        val chatsFlow = getChatsUseCase().cachedIn(viewModelScope)
        updateChatState { it.copy(chats = chatsFlow) }
    }

    override fun handleIntent(intent: ChatUiIntent) {
        when (intent) {
            is ChatUiIntent.OnInputChanged -> {
                updateChatState { it.copy(inputText = intent.text) }
            }

            ChatUiIntent.OnSendClicked -> {
                sendMessage()
            }

            is ChatUiIntent.OnChatSelected -> {
                selectChat(intent.chatId)
            }

            ChatUiIntent.OnNewChatClicked -> {
                createNewChat()
            }

            is ChatUiIntent.OnDrawerStateChanged -> {
                updateChatState { it.copy(isDrawerOpen = intent.isOpen) }
            }

            is ChatUiIntent.OnDeleteChatClicked -> {
                deleteChat(intent.chatId)
            }

            is ChatUiIntent.OnRenameChatClicked -> {
                renameChat(intent.chatId, intent.newTitle)
            }

            is ChatUiIntent.ShowRenameDialog -> {
                updateChatState { it.copy(renameDialogChatId = intent.chatId) }
            }

            ChatUiIntent.DismissRenameDialog -> {
                updateChatState { it.copy(renameDialogChatId = null) }
            }
        }
    }

    private fun selectChat(chatId: String) {
        val messagesFlow = getMessagesUseCase(chatId)
            .map { pagingData ->
                pagingData.map { message ->
                    MessageUiState(
                        id = message.id,
                        text = message.text,
                        isFromUser = message.isFromUser,
                        timestamp = message.timestamp,
                        wasSearchTriggered = message.wasSearchTriggered,
                        products = message.products.map { product ->
                            ProductItemUiState(
                                id = product.id,
                                name = product.name,
                                price = product.price,
                                imageUrl = product.imageUrl,
                                rating = product.rating,
                                numReviews = product.numReviews,
                                url = product.url
                            )
                        }
                    )
                }
            }
            .cachedIn(viewModelScope)

        viewModelScope.launch {
            val chat = getChatByIdUseCase(chatId).getOrNull()
            updateChatState {
                it.copy(
                    selectedChatId = chatId,
                    selectedChatTitle = chat?.title,
                    messages = messagesFlow,
                    isDrawerOpen = false
                )
            }
        }
    }

    private fun createNewChat() {
        updateChatState {
            it.copy(
                selectedChatId = null,
                selectedChatTitle = null,
                messages = emptyFlow(),
                isDrawerOpen = false
            )
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            deleteChatUseCase(chatId).onFailure { error ->
                sendEffect(ChatUiEffect.ShowError(error.message ?: "Failed to delete chat"))
            }
            if (uiState.value.selectedChatId == chatId) {
                createNewChat()
            }
        }
    }

    private fun renameChat(chatId: String, newTitle: String) {
        viewModelScope.launch {
            renameChatUseCase(chatId, newTitle)
                .onSuccess {
                    if (uiState.value.selectedChatId == chatId) {
                        updateChatState { it.copy(selectedChatTitle = newTitle, renameDialogChatId = null) }
                    } else {
                        updateChatState { it.copy(renameDialogChatId = null) }
                    }
                }
                .onFailure { error ->
                    sendEffect(ChatUiEffect.ShowError(error.message ?: "Failed to rename chat"))
                    updateChatState { it.copy(renameDialogChatId = null) }
                }
        }
    }

    private fun sendMessage() {
        val text = uiState.value.inputText
        if (text.isBlank() || uiState.value.isLoading) return

        updateChatState { it.copy(inputText = "", isLoading = true) }

        viewModelScope.launch {
            val chatId = uiState.value.selectedChatId ?: createChatUseCase().onSuccess { chatId ->
                selectChat(chatId)
            }.onFailure {
                sendEffect(ChatUiEffect.ShowError(it.message ?: "Failed to create chat"))
            }.getOrNull() ?: return@launch

            sendMessageUseCase(chatId, text)
                .onFailure { error ->
                    sendEffect(ChatUiEffect.ShowError(error.message ?: "Unknown error occurred"))
                }
        }.invokeOnCompletion {
            updateChatState { it.copy(isLoading = false) }
        }
    }

    private fun updateChatState(reducer: (ChatUiState) -> ChatUiState) {
        updateState { state ->
            val newState = reducer(state)
            newState.copy(
                isSendEnabled = newState.inputText.isNotBlank() && !newState.isLoading
            )
        }
    }
}

//class RunSafeScope<R>(
//    val result: Result<R>,
//    val originalException: Throwable?
//) {
//    /**
//     * Executes when the operation finishes successfully.
//     */
//    inline fun onSuccess(action: (value: R) -> Unit): RunSafeScope<R> {
//        result.onSuccess(action)
//        return this
//    }
//
//    /**
//     * Executes for standard runtime failures, ignoring cancellation.
//     */
//    inline fun onError(action: (exception: Throwable) -> Unit): RunSafeScope<R> {
//        if (originalException != null && originalException !is CancellationException) {
//            action(originalException)
//        }
//        return this
//    }
//
//    /**
//     * Executes specifically if the operation or parent coroutine was cancelled.
//     */
//    inline fun onCancel(action: (exception: CancellationException) -> Unit): RunSafeScope<R> {
//        if (originalException is CancellationException) {
//            action(originalException)
//        }
//        return this
//    }
//
//    /**
//     * Executes at the absolute end of the chain, regardless of success, failure, or cancellation.
//     */
//    inline fun onFinally(action: () -> Unit): RunSafeScope<R> {
//        action()
//        return this
//    }
//
//    fun getOrNull(): R? = result.getOrNull()
//}
//
///**
// * Entry point for the runSafe DSL.
// */
//inline fun <R> runSafe(block: () -> R): RunSafeScope<R> {
//    var originalException: Throwable? = null
//    val result = try {
//        Result.success(block())
//    } catch (e: Throwable) {
//        originalException = e
//        Result.failure(e)
//    }
//
//    val scope = RunSafeScope(result, originalException)
//
//    // If it was a cancellation exception, we must re-throw it
//    // after returning the scope so downstream hooks can process it first.
//    if (originalException is CancellationException) {
//        throw originalException
//    }
//
//    return scope
//}
