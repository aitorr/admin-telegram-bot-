package com.aitorr.admintelegrambot.infrastructure.client

import com.aitorr.admintelegrambot.domain.model.TelegramResponse
import com.aitorr.admintelegrambot.domain.model.User
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
     * A simple method for testing your bot's authentication token.
     * Returns basic information about the bot in form of a User object.
     */
    fun getMe(): TelegramResponse<User> {
        val url = "$baseUrl/getMe"
        val response = restTemplate.getForEntity<TelegramResponse<User>>(url)
        return response.body ?: throw RuntimeException("Failed to get bot information")
    }
}
