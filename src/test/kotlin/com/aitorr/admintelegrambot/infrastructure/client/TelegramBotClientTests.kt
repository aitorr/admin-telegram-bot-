package com.aitorr.admintelegrambot.infrastructure.client

import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TelegramBotClientTests {

    @Autowired
    private lateinit var telegramBotClient: TelegramBotClient

    @Autowired
    private lateinit var telegramBotProperties: TelegramBotProperties

    @Test
    fun `test telegram bot client bean is created`() {
        // Verify that the client is properly instantiated
        assert(telegramBotClient != null) { "TelegramBotClient should be created" }
    }

    @Test
    fun `test properties are loaded correctly`() {
        assert(telegramBotProperties.token.isNotEmpty()) { "Token should be configured" }
    }

    // Note: Integration tests that make real API calls should be run separately
    // with @Tag("integration") and excluded from regular builds
    // Example:
    // @Test
    // @Tag("integration")
    // fun `test getMe returns bot information`() {
    //     val response = telegramBotClient.getMe()
    //     assert(response.ok) { "Response should be ok" }
    //     assert(response.result != null) { "Result should not be null" }
    //     assert(response.result!!.isBot) { "Result should be a bot" }
    // }
}
