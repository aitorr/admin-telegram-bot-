package com.aitorr.admintelegrambot.infrastructure.client

import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@Component
class TelegramBotClient(
    private val telegramBotProperties: TelegramBotProperties,
    private val restTemplate: RestTemplate = RestTemplate()
) {
    private val baseUrl = "https://api.telegram.org/bot${telegramBotProperties.token}"

    /**
     * Telegram API response wrapper
     */
    data class TelegramResponse<T>(
        val ok: Boolean,
        val result: T? = null,
        val description: String? = null,
        val errorCode: Int? = null
    )

    /**
     * Telegram User representation (infrastructure model)
     */
    data class User(
        val id: Long,
        val isBot: Boolean,
        val firstName: String,
        val lastName: String? = null,
        val username: String? = null,
        val languageCode: String? = null,
        val canJoinGroups: Boolean? = null,
        val canReadAllGroupMessages: Boolean? = null,
        val supportsInlineQueries: Boolean? = null
    )

    /**
     * A simple method for testing your bot's authentication token.
     * Returns basic information about the bot in form of a User object.
     */
    fun getMe(): TelegramResponse<User> {
        val url = "$baseUrl/getMe"
        try {
            val response = restTemplate.getForEntity<TelegramResponse<User>>(url)
            return response.body ?: throw RuntimeException(
                "Failed to get bot information: Empty response body (HTTP ${response.statusCode})"
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to call Telegram API getMe: ${e.message}", e)
        }
    }
}
