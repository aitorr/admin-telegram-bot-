package com.aitorr.admintelegrambot.domain.port

import arrow.core.Either
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser

interface GetChatBot {
    fun getChatBot(): Either<GetChatBotError, ChatBotUser>
}
sealed class GetChatBotError : BaseError {
    data class UnexpectedError(
            override val message: String,
            val cause: Throwable? = null,
            override val sourceError: BaseError? = null
    ) : GetChatBotError()

    data class TechnicalError(
            override val message: String,
            val errorCode: Int? = null,
            override val sourceError: BaseError? = null
    ) : GetChatBotError()

    data class ChatBotNotFoundError(
            override val message: String,
            override val sourceError: BaseError? = null
    ) : GetChatBotError()
}
