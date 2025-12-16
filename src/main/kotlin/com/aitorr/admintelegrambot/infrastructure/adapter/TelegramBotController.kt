package com.aitorr.admintelegrambot.infrastructure.adapter

import com.aitorr.admintelegrambot.application.GetBotInfoUseCase
import com.aitorr.admintelegrambot.application.GetBotInfoUseCase.GetBotInfoUseCaseError
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

    private fun handleError(error: GetBotInfoUseCaseError): ResponseEntity<ErrorResponse> {
        return when (error) {
            is GetBotInfoUseCaseError.ChatBotDoesNotExistError -> 
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse(
                        code = "CHAT_BOT_DOES_NOT_EXIST",
                        message = error.message,
                        errorTrace = error.toErrorTrace()
                    ))
            
            is GetBotInfoUseCaseError.UnexpectedUseCaseError -> 
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse(
                        code = "UNEXPECTED_USE_CASE_ERROR",
                        message = error.message,
                        errorTrace = error.toErrorTrace()
                    ))
        }
    }

    data class ErrorResponse(
        val code: String,
        val message: String,
        val errorTrace: String? = null
    )
}
