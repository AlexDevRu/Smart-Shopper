package com.example.smartshopper.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.domain.model.Chat
import com.example.smartshopper.R
import kotlinx.coroutines.flow.flowOf
import java.time.Instant

@Composable
fun ChatDrawerContent(
    chats: LazyPagingItems<Chat>,
    selectedChatId: String?,
    onChatSelected: (String) -> Unit,
    onNewChatClicked: () -> Unit,
    onRenameChatClicked: (String) -> Unit,
    onDeleteChatClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavigationDrawerItem(
                selected = selectedChatId.isNullOrEmpty(),
                label = {
                    Text(
                        text = stringResource(R.string.chat_new_chat),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                icon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                onClick = onNewChatClicked
            )

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    count = chats.itemCount,
                    key = chats.itemKey { it.id }
                ) { index ->
                    val chat = chats[index]
                    if (chat != null) {
                        var showMenu by remember { mutableStateOf(false) }

                        NavigationDrawerItem(
                            label = {
                                Column {
                                    Text(
                                        text = if (chat.title.isBlank()) {
                                            stringResource(R.string.chat_new_chat)
                                        } else {
                                            chat.title
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (chat.lastMessage.isNotEmpty()) {
                                        Text(
                                            text = chat.lastMessage,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            selected = chat.id == selectedChatId,
                            onClick = { onChatSelected(chat.id) },
                            badge = {
                                Box {
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
                                                onRenameChatClicked(chat.id)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.chat_delete)) },
                                            onClick = {
                                                showMenu = false
                                                onDeleteChatClicked(chat.id)
                                            }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatDrawerContentPreview() {
    val mockChats = flowOf(
        PagingData.from(
            listOf(
                Chat("1", "Recent Chat", "Hello world", Instant.now()),
                Chat("2", "Older Chat", "Goodbye world", Instant.now().minusSeconds(3600))
            )
        )
    ).collectAsLazyPagingItems()

    ChatDrawerContent(
        chats = mockChats,
        selectedChatId = "1",
        onChatSelected = {},
        onNewChatClicked = {},
        onRenameChatClicked = {},
        onDeleteChatClicked = {}
    )
}
