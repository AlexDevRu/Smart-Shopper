package com.example.data.repository

import com.example.domain.model.AiIntent
import com.example.domain.model.AiResult
import com.example.domain.model.Message
import com.example.domain.model.Price
import com.example.domain.repository.AiDataSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiDataSourceImpl @Inject constructor() : AiDataSource {

    private val updateShoppingPreferencesTool = FunctionDeclaration(
        name = TOOL_NAME,
        description = TOOL_DESCRIPTION,
        parameters = mapOf(
            PARAM_PRODUCT_NAME to Schema.string(description = PARAM_PRODUCT_NAME_DESCRIPTION),
            PARAM_MAX_PRICE to Schema.obj(
                mapOf(
                    PARAM_VALUE to Schema.double(description = PARAM_VALUE_DESCRIPTION),
                    PARAM_CURRENCY to Schema.string(description = PARAM_CURRENCY_DESCRIPTION)
                ),
                description = PARAM_MAX_PRICE_DESCRIPTION
            )
        ),
        optionalParameters = listOf(PARAM_MAX_PRICE)
    )

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
        modelName = MODEL_NAME,
        tools = listOf(Tool.functionDeclarations(listOf(updateShoppingPreferencesTool))),
        systemInstruction = content {
            text(SYSTEM_INSTRUCTION)
        }
    )

    override suspend fun generateResponse(
        userInput: String,
        history: List<Message>
    ): AiResult {
        val chatHistory = history.map { message ->
            content(role = if (message.isFromUser) "user" else "model") {
                text(message.text)
            }
        }
        val chat = generativeModel.startChat(chatHistory)

        val response = chat.sendMessage(userInput)

        var extractedIntent: AiIntent? = null

        val functionCalls = response.functionCalls
        if (functionCalls.isNotEmpty()) {
            val toolCall = functionCalls.find { it.name == TOOL_NAME }
            toolCall?.let { call ->
                val productName = call.args[PARAM_PRODUCT_NAME]!!.jsonPrimitive.content

                val maxPrice = call.args[PARAM_MAX_PRICE]?.jsonObject?.let {
                    Price(
                        value = it[PARAM_VALUE]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                        currency = it[PARAM_CURRENCY]?.jsonPrimitive?.content.orEmpty()
                    )
                }

                extractedIntent = AiIntent.ShoppingIntent(productName, maxPrice)
            }
        }

        val responseText = response.text.orEmpty()
        return AiResult(responseText, extractedIntent)
    }

    companion object {
        private const val MODEL_NAME = "gemini-3.6-flash"

        private const val TOOL_NAME = "update_shopping_preferences"
        private const val TOOL_DESCRIPTION = "Record the product name and budget the user is looking for."

        private const val SYSTEM_INSTRUCTION = "You are a helpful shopping assistant. Your goal is to help users find the best products. " +
                "When a user mentions a product they want to buy or a budget, always call " +
                "'$TOOL_NAME' to record these details."

        private const val PARAM_PRODUCT_NAME = "product_name"
        private const val PARAM_PRODUCT_NAME_DESCRIPTION = "The name of the product the user wants to buy."

        private const val PARAM_MAX_PRICE = "max_price"
        private const val PARAM_MAX_PRICE_DESCRIPTION = "The maximum budget specified by the user."

        private const val PARAM_VALUE = "value"
        private const val PARAM_VALUE_DESCRIPTION = "The numerical amount of the maximum price. Must be a positive number (e.g., 49.99)."

        private const val PARAM_CURRENCY = "currency"
        private const val PARAM_CURRENCY_DESCRIPTION = "The currency of the budget (e.g., USD, EUR)."
    }
}
