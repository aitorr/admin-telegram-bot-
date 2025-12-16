package com.aitorr.admintelegrambot.infrastructure.client

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import com.aitorr.admintelegrambot.domain.port.GetChatBot.GetChatBotError
import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@Component
class TelegramBotClient(
    private val telegramBotProperties: TelegramBotProperties,
    private val restTemplate: RestTemplate = RestTemplate()
) : GetChatBot {
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
     * Implementation of GetChatBot port
     * Returns Either with error on the left or ChatBotUser on the right
     */
    override fun getChatBot(): Either<GetChatBotError, ChatBotUser> {
        return try {
            val response = getMe()
            
            if (response.ok && response.result != null) {
                val user = response.result
                ChatBotUser(
                    id = user.id,
                    isBot = user.isBot,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    username = user.username,
                    languageCode = user.languageCode
                ).right()
            } else {
                when {
                    response.errorCode == 404 -> GetChatBotError.ChatBotNotFoundError(
                        response.description ?: "Chat bot not found"
                    ).left()
                    response.errorCode != null -> GetChatBotError.TechnicalError(
                        response.description ?: "Technical error occurred",
                        response.errorCode
                    ).left()
                    else -> GetChatBotError.TechnicalError(
                        response.description ?: "Unknown technical error"
                    ).left()
                }
            }
        } catch (e: HttpClientErrorException) {
            when (e.statusCode) {
                HttpStatus.NOT_FOUND -> GetChatBotError.ChatBotNotFoundError(
                    "Chat bot not found: ${e.message}"
                ).left()
                else -> GetChatBotError.TechnicalError(
                    "HTTP error: ${e.message}",
                    e.statusCode.value()
                ).left()
            }
        } catch (e: Exception) {
            GetChatBotError.UnexpectedError(
                "Unexpected error calling Telegram API: ${e.message}",
                e
            ).left()
        }
    }

    /**
     * Internal method for calling Telegram getMe API
     */
    private fun getMe(): TelegramResponse<User> {
        val url = "$baseUrl/getMe"
        val response = restTemplate.getForEntity<TelegramResponse<User>>(url)
        return response.body ?: throw RuntimeException(
            "Failed to get bot information: Empty response body (HTTP ${response.statusCode})"
        )
    }
}
