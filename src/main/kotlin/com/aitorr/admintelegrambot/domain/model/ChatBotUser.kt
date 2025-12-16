package com.aitorr.admintelegrambot.domain.model

/**
 * Domain model representing a chat bot user
 */
data class ChatBotUser(
    val id: Long,
    val isBot: Boolean,
    val firstName: String,
    val lastName: String? = null,
    val username: String? = null,
    val languageCode: String? = null
)
