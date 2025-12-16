package com.aitorr.admintelegrambot.application

import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.infrastructure.client.TelegramBotClient
import org.springframework.stereotype.Service

@Service
class GetBotInfoUseCase(
    private val telegramBotClient: TelegramBotClient
) {
    /**
     * Get information about the bot
     * Returns a domain model ChatBotUser
     */
    fun execute(): ChatBotUser {
        val response = telegramBotClient.getMe()
        if (response.ok && response.result != null) {
            val user = response.result
            return ChatBotUser(
                id = user.id,
                isBot = user.isBot,
                firstName = user.firstName,
                lastName = user.lastName,
                username = user.username,
                languageCode = user.languageCode
            )
        } else {
            throw RuntimeException("Failed to get bot info: ${response.description}")
        }
    }
}
