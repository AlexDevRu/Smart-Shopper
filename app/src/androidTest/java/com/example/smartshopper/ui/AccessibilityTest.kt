package com.example.smartshopper.ui

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartshopper.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AccessibilityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    companion object {
        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            AccessibilityChecks.enable()
        }
    }

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testChatScreenAccessibility() {
        // AccessibilityChecks.enable() will automatically run on every interaction.
        // We perform a simple interaction like opening the menu to trigger the audit.
        
        composeTestRule
            .onNodeWithContentDescription("Menu")
            .performClick()

        // If there are accessibility violations (e.g. small touch targets), 
        // the test will fail here.
    }
}
