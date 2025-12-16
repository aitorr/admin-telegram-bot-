package com.aitorr.admintelegrambot.infrastructure.adapter

import com.aitorr.admintelegrambot.application.GetBotInfoUseCase
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot.GetChatBotError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telegram")
class TelegramBotController(
    private val getBotInfoUseCase: GetBotInfoUseCase
) {
    @GetMapping("/bot-info")
    fun getBotInfo(): ResponseEntity<*> {
        return getBotInfoUseCase.execute().fold(
            ifLeft = { error -> handleError(error) },
            ifRight = { user -> ResponseEntity.ok(user) }
        )
    }

    private fun handleError(error: GetChatBotError): ResponseEntity<ErrorResponse> {
        return when (error) {
            is GetChatBotError.ChatBotNotFoundError -> 
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse("CHAT_BOT_NOT_FOUND", error.message))
            
            is GetChatBotError.TechnicalError -> 
                ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(ErrorResponse(
                        "TECHNICAL_ERROR", 
                        error.message,
                        error.errorCode
                    ))
            
            is GetChatBotError.UnexpectedError -> 
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("UNEXPECTED_ERROR", error.message))
        }
    }

    data class ErrorResponse(
        val code: String,
        val message: String,
        val errorCode: Int? = null
    )
}
