package com.aitorr.admintelegrambot.application

import arrow.core.Either
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import org.springframework.stereotype.Service

@Service
class GetBotInfoUseCase(
    private val getChatBot: GetChatBot
) {
    fun execute(): Either<GetBotInfoUseCaseError, ChatBotUser> {
        return getChatBot.getChatBot().mapLeft { portError ->
            when (portError) {
                is GetChatBot.GetChatBotError.ChatBotNotFoundError ->
                    GetBotInfoUseCaseError.ChatBotDoesNotExistError(
                        message = "Chat bot does not exist",
                        sourceError = portError
                    )
                is GetChatBot.GetChatBotError.TechnicalError,
                is GetChatBot.GetChatBotError.UnexpectedError ->
                    GetBotInfoUseCaseError.UnexpectedUseCaseError(
                        message = "Unexpected error retrieving bot info",
                        sourceError = portError
                    )
            }
        }
    }
    
    sealed class GetBotInfoUseCaseError : BaseError {
        data class ChatBotDoesNotExistError(
            override val message: String,
            override val sourceError: BaseError? = null
        ) : GetBotInfoUseCaseError()
        
        data class UnexpectedUseCaseError(
            override val message: String,
            override val sourceError: BaseError? = null
        ) : GetBotInfoUseCaseError()
    }
}
