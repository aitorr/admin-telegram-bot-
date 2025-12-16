package com.aitorr.admintelegrambot.infrastructure.adapter

import com.aitorr.admintelegrambot.application.TelegramBotService
import com.aitorr.admintelegrambot.domain.model.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telegram")
class TelegramBotController(
    private val telegramBotService: TelegramBotService
) {
    @GetMapping("/bot-info")
    fun getBotInfo(): User {
        return telegramBotService.getBotInfo()
    }
}
