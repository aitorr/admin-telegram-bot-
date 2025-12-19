package com.aitorr.admintelegrambot.infrastructure.adapter.outbound

import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.infrastructure.jooq.tables.ChatBotUsers.Companion.CHAT_BOT_USERS
import com.aitorr.admintelegrambot.infrastructure.jooq.tables.pojos.ChatBotUsers as ChatBotUsersPojo
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * jOOQ Repository for ChatBotUser
 * 
 * This repository uses jOOQ for database operations with type-safe SQL queries.
 */
@Repository
class ChatBotUserRepositoryJooq(private val dsl: DSLContext) {

    /**
     * Save a chat bot user to the database
     */
    fun save(chatBotUser: ChatBotUser): ChatBotUser {
        dsl.insertInto(CHAT_BOT_USERS)
            .set(CHAT_BOT_USERS.ID, chatBotUser.id)
            .set(CHAT_BOT_USERS.IS_BOT, chatBotUser.isBot)
            .set(CHAT_BOT_USERS.FIRST_NAME, chatBotUser.firstName)
            .set(CHAT_BOT_USERS.LAST_NAME, chatBotUser.lastName)
            .set(CHAT_BOT_USERS.USERNAME, chatBotUser.username)
            .set(CHAT_BOT_USERS.LANGUAGE_CODE, chatBotUser.languageCode)
            .onConflict(CHAT_BOT_USERS.ID)
            .doUpdate()
            .set(CHAT_BOT_USERS.FIRST_NAME, chatBotUser.firstName)
            .set(CHAT_BOT_USERS.LAST_NAME, chatBotUser.lastName)
            .set(CHAT_BOT_USERS.USERNAME, chatBotUser.username)
            .set(CHAT_BOT_USERS.LANGUAGE_CODE, chatBotUser.languageCode)
            .execute()
        
        return chatBotUser
    }

    /**
     * Find a chat bot user by ID
     */
    fun findById(id: Long): ChatBotUser? {
        return dsl.selectFrom(CHAT_BOT_USERS)
            .where(CHAT_BOT_USERS.ID.eq(id))
            .fetchOne()
            ?.let { toDomain(it.into(ChatBotUsersPojo::class.java)) }
    }

    /**
     * Find all chat bot users
     */
    fun findAll(): List<ChatBotUser> {
        return dsl.selectFrom(CHAT_BOT_USERS)
            .fetch()
            .map { toDomain(it.into(ChatBotUsersPojo::class.java)) }
    }

    /**
     * Update a chat bot user
     */
    fun update(chatBotUser: ChatBotUser): ChatBotUser {
        dsl.update(CHAT_BOT_USERS)
            .set(CHAT_BOT_USERS.FIRST_NAME, chatBotUser.firstName)
            .set(CHAT_BOT_USERS.LAST_NAME, chatBotUser.lastName)
            .set(CHAT_BOT_USERS.USERNAME, chatBotUser.username)
            .set(CHAT_BOT_USERS.LANGUAGE_CODE, chatBotUser.languageCode)
            .where(CHAT_BOT_USERS.ID.eq(chatBotUser.id))
            .execute()
        
        return chatBotUser
    }

    /**
     * Delete a chat bot user by ID
     */
    fun deleteById(id: Long): Boolean {
        val rowsDeleted = dsl.deleteFrom(CHAT_BOT_USERS)
            .where(CHAT_BOT_USERS.ID.eq(id))
            .execute()
        
        return rowsDeleted > 0
    }

    /**
     * Delete all chat bot users
     */
    fun deleteAll() {
        dsl.deleteFrom(CHAT_BOT_USERS).execute()
    }

    /**
     * Count all chat bot users
     */
    fun count(): Long {
        return dsl.selectCount()
            .from(CHAT_BOT_USERS)
            .fetchOne(0, Long::class.java) ?: 0L
    }

    /**
     * Check if a chat bot user exists by ID
     */
    fun existsById(id: Long): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(CHAT_BOT_USERS)
                .where(CHAT_BOT_USERS.ID.eq(id))
        )
    }

    /**
     * Convert jOOQ POJO to domain model
     */
    private fun toDomain(pojo: ChatBotUsersPojo): ChatBotUser {
        return ChatBotUser(
            id = pojo.id ?: 0L,
            isBot = pojo.isBot ?: false,
            firstName = pojo.firstName ?: "",
            lastName = pojo.lastName,
            username = pojo.username,
            languageCode = pojo.languageCode
        )
    }
}
