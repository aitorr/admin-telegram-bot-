package com.aitorr.admintelegrambot.domain.port

import arrow.core.Either
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser

/**
 * Port for retrieving chat bot information
 * Follows hexagonal architecture - this interface is defined in the domain layer
 */
interface GetChatBot {
    
    /**
     * Get chat bot information
     * Returns Either with error on the left or ChatBotUser on the right
     */
    fun getChatBot(): Either<GetChatBotError, ChatBotUser>
    
    /**
     * Sealed class hierarchy for GetChatBot port errors
     * These are infrastructure-level errors
     */
    sealed class GetChatBotError : BaseError {
        /**
         * Unexpected error that couldn't be categorized
         */
        data class UnexpectedError(
            override val message: String,
            val cause: Throwable? = null,
            override val sourceError: BaseError? = null
        ) : GetChatBotError()
        
        /**
         * Technical error from infrastructure layer (network, API, etc.)
         */
        data class TechnicalError(
            override val message: String,
            val errorCode: Int? = null,
            override val sourceError: BaseError? = null
        ) : GetChatBotError()
        
        /**
         * Chat bot not found
         */
        data class ChatBotNotFoundError(
            override val message: String,
            override val sourceError: BaseError? = null
        ) : GetChatBotError()
    }
}
