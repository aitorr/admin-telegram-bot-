package com.aitorr.admintelegrambot.application

import arrow.core.Either
import arrow.core.left
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import org.springframework.stereotype.Service

@Service
class GetBotInfoUseCase(
    private val getChatBot: GetChatBot
) {
    /**
     * Get information about the bot
     * Returns Either with use case error on the left or ChatBotUser on the right
     */
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
    
    /**
     * Sealed class hierarchy for GetBotInfoUseCase errors
     * These are application-level errors
     */
    sealed class GetBotInfoUseCaseError : BaseError {
        /**
         * Chat bot does not exist
         */
        data class ChatBotDoesNotExistError(
            override val message: String,
            override val sourceError: BaseError? = null
        ) : GetBotInfoUseCaseError()
        
        /**
         * Unexpected use case error
         */
        data class UnexpectedUseCaseError(
            override val message: String,
            override val sourceError: BaseError? = null
        ) : GetBotInfoUseCaseError()
    }
}
