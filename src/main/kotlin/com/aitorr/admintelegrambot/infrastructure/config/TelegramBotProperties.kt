package com.aitorr.admintelegrambot.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProperties(
    var token: String = ""
)
