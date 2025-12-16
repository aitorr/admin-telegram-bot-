package com.aitorr.admintelegrambot.infrastructure.client

import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TelegramBotClientTests {

    @Autowired
    private lateinit var telegramBotClient: TelegramBotClient

    @Autowired
    private lateinit var telegramBotProperties: TelegramBotProperties

    @Test
    fun `test getMe returns bot information`() {
        // This test will make a real API call to Telegram
        // It verifies that the configuration is correct and the API is reachable
        assertDoesNotThrow {
            val response = telegramBotClient.getMe()
            assert(response.ok) { "Response should be ok" }
            assert(response.result != null) { "Result should not be null" }
            assert(response.result!!.isBot) { "Result should be a bot" }
            println("Bot info: ${response.result}")
        }
    }

    @Test
    fun `test properties are loaded correctly`() {
        assert(telegramBotProperties.token.isNotEmpty()) { "Token should be configured" }
    }
}
