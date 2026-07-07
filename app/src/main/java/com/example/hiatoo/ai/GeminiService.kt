package com.example.hiatoo.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(val apiKey: String) {

    private val systemInstruction = """
        Your name is HIATOO.
        You are a highly intelligent, calm, and polite AI assistant.
        You must ALWAYS address the user as "Sir".
        Examples:
        "Yes Sir."
        "Good Morning Sir."
        "How may I help you today, Sir?"
        Keep your answers relatively concise, as they will be spoken out loud.
    """.trimIndent()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        systemInstruction = content { text(systemInstruction) }
    )

    private val chat = generativeModel.startChat()

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
                return@withContext "I am sorry Sir, but my Gemini API key has not been configured in the settings."
            }
            
            val response = chat.sendMessage(prompt)
            response.text ?: "I am sorry Sir, I was unable to generate a response."
        } catch (e: Exception) {
            e.printStackTrace()
            "I encountered an error connecting to my core processor, Sir."
        }
    }
}
