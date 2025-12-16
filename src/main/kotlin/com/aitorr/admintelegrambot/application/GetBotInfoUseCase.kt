package com.aitorr.admintelegrambot.application

import arrow.core.Either
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import org.springframework.stereotype.Service

@Service
class GetBotInfoUseCase(
    private val getChatBot: GetChatBot
) {
    /**
     * Get information about the bot
     * Returns Either with error on the left or ChatBotUser on the right
     */
    fun execute(): Either<GetChatBot.GetChatBotError, ChatBotUser> {
        return getChatBot.getChatBot()
    }
}
