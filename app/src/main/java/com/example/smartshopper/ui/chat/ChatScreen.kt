package com.example.smartshopper.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.smartshopper.R
import com.example.smartshopper.ui.chat.components.ChatDrawerContent
import com.example.smartshopper.ui.chat.components.ChatInputBar
import com.example.smartshopper.ui.chat.components.ChatMessageItem
import com.example.smartshopper.ui.chat.components.RenameChatDialog
import com.example.smartshopper.ui.theme.SmartShopperTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatUiEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen && drawerState.isClosed) {
            drawerState.open()
        } else if (!state.isDrawerOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        val isOpen = drawerState.currentValue == DrawerValue.Open
        if (isOpen != state.isDrawerOpen) {
            viewModel.sendIntent(ChatUiIntent.OnDrawerStateChanged(isOpen))
        }
    }

    val chats = state.chats.collectAsLazyPagingItems()
    val messages = state.messages.collectAsLazyPagingItems()

    if (state.renameDialogChatId != null) {
        RenameChatDialog(
            initialTitle = state.selectedChatTitle ?: "",
            onConfirm = { newTitle ->
                viewModel.sendIntent(ChatUiIntent.OnRenameChatClicked(state.renameDialogChatId!!, newTitle))
            },
            onDismiss = { viewModel.sendIntent(ChatUiIntent.DismissRenameDialog) }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatDrawerContent(
                chats = chats,
                selectedChatId = state.selectedChatId,
                onChatSelected = { viewModel.sendIntent(ChatUiIntent.OnChatSelected(it)) },
                onNewChatClicked = { viewModel.sendIntent(ChatUiIntent.OnNewChatClicked) },
                onRenameChatClicked = { viewModel.sendIntent(ChatUiIntent.ShowRenameDialog(it)) },
                onDeleteChatClicked = { viewModel.sendIntent(ChatUiIntent.OnDeleteChatClicked(it)) }
            )
        }
    ) {
        ChatScreenContent(
            state = state,
            messages = messages,
            snackbarHostState = snackbarHostState,
            onMenuClick = { scope.launch { drawerState.open() } },
            onInputChanged = { viewModel.sendIntent(ChatUiIntent.OnInputChanged(it)) },
            onSendClicked = { viewModel.sendIntent(ChatUiIntent.OnSendClicked) },
            onDeleteChat = { state.selectedChatId?.let { viewModel.sendIntent(ChatUiIntent.OnDeleteChatClicked(it)) } },
            onRenameChat = { state.selectedChatId?.let { viewModel.sendIntent(ChatUiIntent.ShowRenameDialog(it)) } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreenContent(
    state: ChatUiState,
    messages: LazyPagingItems<MessageUiState>,
    snackbarHostState: SnackbarHostState,
    onMenuClick: () -> Unit,
    onInputChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onDeleteChat: () -> Unit,
    onRenameChat: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val title = if (state.selectedChatId == null || state.selectedChatTitle.isNullOrBlank()) {
                        stringResource(R.string.chat_new_chat)
                    } else {
                        state.selectedChatTitle
                    }
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.chat_menu_description)
                        )
                    }
                },
                actions = {
                    if (state.selectedChatId != null) {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.chat_more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_rename)) },
                                onClick = {
                                    showMenu = false
                                    onRenameChat()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_delete)) },
                                onClick = {
                                    showMenu = false
                                    onDeleteChat()
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                text = state.inputText,
                isSendEnabled = state.isSendEnabled,
                onTextChanged = onInputChanged,
                onSendClicked = onSendClicked
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (messages.itemCount == 0 && !state.isLoading) {
                Text(
                    text = stringResource(R.string.chat_welcome_message),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    reverseLayout = true
                ) {
                    if (state.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    items(
                        count = messages.itemCount,
                        key = messages.itemKey { it.id }
                    ) { index ->
                        val message = messages[index]
                        if (message != null) {
                            ChatMessageItem(message = message)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    val flow = remember {
        MutableStateFlow(
            PagingData.from(
                listOf(
                    MessageUiState("2", "Hi there!", true, Instant.now()),
                    MessageUiState("1", "Hello!", false, Instant.now().minusSeconds(60))
                )
            )
        )
    }

    val mockMessages = flow.collectAsLazyPagingItems()

    SmartShopperTheme {
        ChatScreenContent(
            state = ChatUiState(
                selectedChatId = "1",
                inputText = "",
                isLoading = false
            ),
            messages = mockMessages,
            snackbarHostState = SnackbarHostState(),
            onMenuClick = {},
            onInputChanged = {},
            onSendClicked = {},
            onDeleteChat = {},
            onRenameChat = {}
        )
    }
}
