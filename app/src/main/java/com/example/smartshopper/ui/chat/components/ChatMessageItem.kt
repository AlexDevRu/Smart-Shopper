package com.example.smartshopper.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartshopper.R
import com.example.smartshopper.ui.chat.MessageUiState
import com.example.smartshopper.ui.chat.ProductItemUiState
import com.example.smartshopper.ui.common.formatTime
import com.example.smartshopper.ui.theme.SmartShopperTheme
import java.time.Instant

@Composable
fun ChatMessageItem(
    message: MessageUiState,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start

    val backgroundColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = alignment
    ) {
        if (message.text.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        if (message.products.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(message.products, key = { it.id }) { product ->
                    ProductCard(product = product)
                }
            }
        } else if (message.wasSearchTriggered) {
            Text(
                text = stringResource(R.string.search_no_results),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            )
        }

        Text(
            text = formatTime(message.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserMessagePreview() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "1",
                text = "I'm looking for a new fridge.",
                isFromUser = true,
                timestamp = Instant.now()
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AiMessagePreview() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "2",
                text = "Sure! I found some fridges for you.",
                isFromUser = false,
                timestamp = Instant.now()
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AiMessageWithProductsPreview() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "3",
                text = "Here are some options:",
                isFromUser = false,
                timestamp = Instant.now(),
                wasSearchTriggered = true,
                products = listOf(
                    ProductItemUiState("1", "Fridge A", "$500", ""),
                    ProductItemUiState("2", "Fridge B", "$600", "")
                )
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AiMessageNoProductsPreview() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "4",
                text = "I couldn't find any products matching your criteria.",
                isFromUser = false,
                timestamp = Instant.now(),
                wasSearchTriggered = true,
                products = emptyList()
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}
