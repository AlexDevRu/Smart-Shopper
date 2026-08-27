package com.example.smartshopper.ui.chat

import androidx.paging.PagingData
import com.example.domain.model.Chat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.time.Instant

data class MessageUiState(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Instant,
    val products: List<ProductItemUiState> = emptyList(),
    val wasSearchTriggered: Boolean = false
)

data class ProductItemUiState(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String,
    val rating: Double? = null,
    val numReviews: Int? = null,
    val url: String = ""
)

data class ChatUiState(
    val selectedChatId: String? = null,
    val selectedChatTitle: String? = null,
    val chats: Flow<PagingData<Chat>> = emptyFlow(),
    val messages: Flow<PagingData<MessageUiState>> = emptyFlow(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSendEnabled: Boolean = false,
    val isDrawerOpen: Boolean = false,
    val renameDialogChatId: String? = null
)

sealed interface ChatUiIntent {
    data class OnInputChanged(val text: String) : ChatUiIntent
    data object OnSendClicked : ChatUiIntent
    data class OnChatSelected(val chatId: String) : ChatUiIntent
    data object OnNewChatClicked : ChatUiIntent
    data class OnDrawerStateChanged(val isOpen: Boolean) : ChatUiIntent
    data class OnDeleteChatClicked(val chatId: String) : ChatUiIntent
    data class OnRenameChatClicked(val chatId: String, val newTitle: String) : ChatUiIntent
    data class ShowRenameDialog(val chatId: String) : ChatUiIntent
    data object DismissRenameDialog : ChatUiIntent
}

sealed interface ChatUiEffect {
    data class ShowError(val message: String) : ChatUiEffect
}
