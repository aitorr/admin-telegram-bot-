package com.aitorr.admintelegrambot.infrastructure.persistence

import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import jakarta.persistence.*

@Entity
@Table(name = "chat_bot_users")
class ChatBotUserEntity(
    @Id
    var id: Long = 0,
    
    @Column(nullable = false)
    var isBot: Boolean = false,
    
    @Column(nullable = false)
    var firstName: String = "",
    
    @Column
    var lastName: String? = null,
    
    @Column
    var username: String? = null,
    
    @Column
    var languageCode: String? = null
) {
    fun toDomain(): ChatBotUser {
        return ChatBotUser(
            id = id,
            isBot = isBot,
            firstName = firstName,
            lastName = lastName,
            username = username,
            languageCode = languageCode
        )
    }
    
    companion object {
        fun fromDomain(chatBotUser: ChatBotUser): ChatBotUserEntity {
            return ChatBotUserEntity(
                id = chatBotUser.id,
                isBot = chatBotUser.isBot,
                firstName = chatBotUser.firstName,
                lastName = chatBotUser.lastName,
                username = chatBotUser.username,
                languageCode = chatBotUser.languageCode
            )
        }
    }
}
