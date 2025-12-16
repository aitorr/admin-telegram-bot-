package com.aitorr.admintelegrambot.domain.port

import arrow.core.Either
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
     * Sealed class hierarchy for GetChatBot errors
     */
    sealed class GetChatBotError {
        /**
         * Unexpected error that couldn't be categorized
         */
        data class UnexpectedError(val message: String, val cause: Throwable? = null) : GetChatBotError()
        
        /**
         * Technical error from infrastructure layer (network, API, etc.)
         */
        data class TechnicalError(val message: String, val errorCode: Int? = null) : GetChatBotError()
        
        /**
         * Chat bot not found
         */
        data class ChatBotNotFoundError(val message: String) : GetChatBotError()
    }
}
