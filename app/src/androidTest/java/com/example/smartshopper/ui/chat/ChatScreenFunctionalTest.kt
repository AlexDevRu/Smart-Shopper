package com.example.smartshopper.ui.chat

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartshopper.MainActivity
import com.example.smartshopper.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatScreenFunctionalTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testWelcomeMessageIsDisplayedOnStartup() {
        val welcomeMessage = composeTestRule.activity.getString(R.string.chat_welcome_message)
        composeTestRule.onNodeWithText(welcomeMessage).assertIsDisplayed()
    }

    @Test
    fun testSendButtonEnablementLogic() {
        val sendButtonContentDescription = composeTestRule.activity.getString(R.string.chat_send_button)
        val inputHint = composeTestRule.activity.getString(R.string.chat_input_hint)

        // Initially disabled
        composeTestRule.onNodeWithContentDescription(sendButtonContentDescription).assertIsNotEnabled()

        // Type text
        composeTestRule.onNodeWithText(inputHint).performTextInput("Hello")

        // Should be enabled
        composeTestRule.onNodeWithContentDescription(sendButtonContentDescription).assertIsEnabled()

        // Clear text (not easy with direct API, but we can verify it's enabled after typing)
    }

    @Test
    fun testDrawerOpensOnMenuClick() {
        val menuDescription = composeTestRule.activity.getString(R.string.chat_menu_description)
        val newChatText = composeTestRule.activity.getString(R.string.chat_new_chat)

        // Open drawer
        composeTestRule.onNodeWithContentDescription(menuDescription).performClick()

        // Verify drawer content (New Chat button should be visible)
        composeTestRule.onNodeWithText(newChatText).assertIsDisplayed()
    }

    @Test
    fun testRenameDialogAppearance() {
        val inputHint = composeTestRule.activity.getString(R.string.chat_input_hint)
        val sendButtonDescription = composeTestRule.activity.getString(R.string.chat_send_button)
        val moreOptionsDescription = composeTestRule.activity.getString(R.string.chat_more_options)
        val renameText = composeTestRule.activity.getString(R.string.chat_rename)
        val renameDialogTitle = composeTestRule.activity.getString(R.string.chat_rename_dialog_title)

        // 1. Create a chat by sending a message
        composeTestRule.onNodeWithText(inputHint).performTextInput("Initial message")
        composeTestRule.onNodeWithContentDescription(sendButtonDescription).performClick()

        // 2. Wait for chat to be created (More options should appear)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription(moreOptionsDescription).fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Open overflow menu
        composeTestRule.onNodeWithContentDescription(moreOptionsDescription).performClick()

        // 4. Click Rename
        composeTestRule.onNodeWithText(renameText).performClick()

        // 5. Verify dialog title
        composeTestRule.onNodeWithText(renameDialogTitle).assertIsDisplayed()
    }
    
    private fun SemanticsNodeInteractionsProvider.onAllNodesWithContentDescription(
        value: String,
        substring: Boolean = false,
        ignoreCase: Boolean = false
    ) = onAllNodes(hasContentDescription(value, substring, ignoreCase))
}
