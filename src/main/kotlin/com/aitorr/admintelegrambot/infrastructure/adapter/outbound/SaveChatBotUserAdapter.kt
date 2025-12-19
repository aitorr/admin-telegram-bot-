package com.aitorr.admintelegrambot.infrastructure.adapter.outbound

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.SaveChatBotUser
import com.aitorr.admintelegrambot.domain.port.SaveChatBotUserError
import org.springframework.stereotype.Component

/**
 * Outbound adapter for saving chat bot users to the database
 */
@Component
class SaveChatBotUserAdapter(
    private val repository: ChatBotUserRepositoryJooq
) : SaveChatBotUser {

    override fun save(chatBotUser: ChatBotUser): Either<SaveChatBotUserError, ChatBotUser> {
        return try {
            val saved = repository.save(chatBotUser)
            saved.right()
        } catch (e: Exception) {
            SaveChatBotUserError.PersistenceError(
                message = "Failed to save chat bot user: ${e.message}",
                cause = e
            ).left()
        }
    }
}
