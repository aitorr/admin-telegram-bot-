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
        assert(telegramBotClient != null) { "TelegramBotClient should be created" }
    }

    @Test
    fun `test properties are loaded correctly`() {
        assert(telegramBotProperties.token.isNotEmpty()) { "Token should be configured" }
    }
}
