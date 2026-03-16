package com.guiuriarte.cozinhei.data.api.model

data class Message(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<Message>
)
