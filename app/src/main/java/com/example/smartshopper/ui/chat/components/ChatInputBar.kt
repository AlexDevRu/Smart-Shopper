package com.example.smartshopper.ui.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartshopper.R
import com.example.smartshopper.ui.theme.SmartShopperTheme

@Composable
fun ChatInputBar(
    text: String,
    isSendEnabled: Boolean,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .safeDrawingPadding()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = { Text(stringResource(R.string.chat_input_hint)) },
            modifier = Modifier.weight(1f),
            maxLines = 4
        )
        IconButton(
            onClick = {
                keyboardController?.hide()
                onSendClicked()
            },
            enabled = isSendEnabled
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.chat_send_button)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatInputBarEmptyPreview() {
    SmartShopperTheme {
        ChatInputBar(
            text = "",
            isSendEnabled = false,
            onTextChanged = {},
            onSendClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatInputBarWithTextPreview() {
    SmartShopperTheme {
        ChatInputBar(
            text = "Hello, I'm looking for some deals!",
            isSendEnabled = true,
            onTextChanged = {},
            onSendClicked = {}
        )
    }
}
