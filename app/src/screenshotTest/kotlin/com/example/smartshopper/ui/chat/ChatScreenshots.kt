package com.example.smartshopper.ui.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.example.smartshopper.ui.chat.components.ChatInputBar
import com.example.smartshopper.ui.chat.components.ChatMessageItem
import com.example.smartshopper.ui.chat.components.ProductCard
import com.example.smartshopper.ui.theme.SmartShopperTheme
import java.time.Instant

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun ProductCardScreenshot() {
    SmartShopperTheme {
        ProductCard(
            product = ProductItemUiState(
                id = "1",
                name = "Samsung Fridge",
                price = "$999",
                imageUrl = "",
                rating = 4.5,
                numReviews = 120,
                url = "https://example.com"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun UserMessageScreenshot() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "1",
                text = "I'm looking for a new fridge.",
                isFromUser = true,
                timestamp = Instant.parse("2026-08-21T12:00:00Z")
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun AiMessageWithProductsScreenshot() {
    SmartShopperTheme {
        ChatMessageItem(
            message = MessageUiState(
                id = "3",
                text = "Here are some options:",
                isFromUser = false,
                timestamp = Instant.parse("2026-08-21T12:01:00Z"),
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

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun ChatInputBarEmptyScreenshot() {
    SmartShopperTheme {
        ChatInputBar(
            text = "",
            isSendEnabled = false,
            onTextChanged = {},
            onSendClicked = {}
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun ChatInputBarTypedScreenshot() {
    SmartShopperTheme {
        ChatInputBar(
            text = "Searching for a bike",
            isSendEnabled = true,
            onTextChanged = {},
            onSendClicked = {}
        )
    }
}
