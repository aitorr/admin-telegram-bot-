package com.aitorr.admintelegrambot

import com.aitorr.admintelegrambot.config.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
class AdminTelegramBotApplicationTests {

    @Test
    fun contextLoads() {
    }
}
