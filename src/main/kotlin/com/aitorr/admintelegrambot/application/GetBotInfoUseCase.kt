package com.aitorr.admintelegrambot.application

import arrow.core.Either
import arrow.core.flatMap
import com.aitorr.admintelegrambot.domain.error.BaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import com.aitorr.admintelegrambot.domain.port.GetChatBotError
import com.aitorr.admintelegrambot.domain.port.GetChatBotError.*
import com.aitorr.admintelegrambot.domain.port.SaveChatBotUser
import com.aitorr.admintelegrambot.domain.port.SaveChatBotUserError
import org.springframework.stereotype.Service

@Service
class GetBotInfoUseCase(
    private val getChatBot: GetChatBot,
    private val saveChatBotUser: SaveChatBotUser
) {
    fun execute(): Either<GetBotInfoUseCaseError, ChatBotUser> {
        return getChatBot.getChatBot()
            .mapLeft { portError ->
                when (portError) {
                    is ChatBotNotFoundError ->
                        GetBotInfoUseCaseError.ChatBotDoesNotExistError(
                            message = "Chat bot does not exist",
                            sourceError = portError
                        )
                    is TechnicalError,
                    is UnexpectedError ->
                        GetBotInfoUseCaseError.UnexpectedUseCaseError(
                            message = "Unexpected error retrieving bot info",
                            sourceError = portError
                        )
                }
            }
            .flatMap { chatBotUser ->
                // Save the bot to database after retrieving it
                saveChatBotUser.save(chatBotUser).mapLeft { saveError ->
                    when (saveError) {
                        is SaveChatBotUserError.PersistenceError,
                        is SaveChatBotUserError.UnexpectedError ->
                            GetBotInfoUseCaseError.UnexpectedUseCaseError(
                                message = "Failed to save bot info to database: ${saveError.message}",
                                sourceError = saveError
                            )
                    }
                }
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