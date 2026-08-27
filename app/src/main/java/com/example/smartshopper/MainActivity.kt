package com.example.smartshopper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.smartshopper.ui.chat.ChatScreen
import com.example.smartshopper.ui.common.DateFormatter
import com.example.smartshopper.ui.common.LocalDateFormatter
import com.example.smartshopper.ui.theme.SmartShopperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dateFormatter = remember { DateFormatter() }

            SmartShopperTheme {
                CompositionLocalProvider(LocalDateFormatter provides dateFormatter) {
                    ChatScreen()
                }
            }
        }
    }
}
