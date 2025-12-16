package com.aitorr.admintelegrambot.infrastructure.adapter

import com.aitorr.admintelegrambot.application.GetBotInfoUseCase
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telegram")
class TelegramBotController(
    private val getBotInfoUseCase: GetBotInfoUseCase
) {
    @GetMapping("/bot-info")
    fun getBotInfo(): ChatBotUser {
        return getBotInfoUseCase.execute()
    }
}
