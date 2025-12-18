package com.aitorr.admintelegrambot.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatBotUserRepository : JpaRepository<ChatBotUserEntity, Long>
