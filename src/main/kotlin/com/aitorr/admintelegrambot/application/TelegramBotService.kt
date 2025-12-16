package com.aitorr.admintelegrambot.application

import com.aitorr.admintelegrambot.domain.model.User
import com.aitorr.admintelegrambot.infrastructure.client.TelegramBotClient
import org.springframework.stereotype.Service

@Service
class TelegramBotService(
    private val telegramBotClient: TelegramBotClient
) {
    /**
     * Get information about the bot
     */
    fun getBotInfo(): User {
        val response = telegramBotClient.getMe()
        if (response.ok && response.result != null) {
            return response.result
        } else {
            throw RuntimeException("Failed to get bot info: ${response.description}")
        }
    }
}
