package com.aitorr.admintelegrambot.domain.model

data class ChatBotUser(
    val id: Long,
    val isBot: Boolean,
    val firstName: String,
    val lastName: String? = null,
    val username: String? = null,
    val languageCode: String? = null
)
