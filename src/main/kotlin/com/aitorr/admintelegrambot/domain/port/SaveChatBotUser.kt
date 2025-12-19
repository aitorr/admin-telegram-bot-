package com.aitorr.admintelegrambot.domain.port

import arrow.core.Either
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser

/**
 * Port for saving chat bot user information to persistence layer
 */
interface SaveChatBotUser {
    fun save(chatBotUser: ChatBotUser): Either<SaveChatBotUserError, ChatBotUser>
}

sealed class SaveChatBotUserError : BaseError {
    data class PersistenceError(
        override val message: String,
        val cause: Throwable? = null,
        override val sourceError: BaseError? = null
    ) : SaveChatBotUserError()

    data class UnexpectedError(
        override val message: String,
        val cause: Throwable? = null,
        override val sourceError: BaseError? = null
    ) : SaveChatBotUserError()
}
