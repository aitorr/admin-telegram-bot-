package com.aitorr.admintelegrambot.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Mock Telegram Bot Server for testing
 * Simulates Telegram API responses without making real HTTP calls
 */
@Component
class MockTelegramBotServer {
    
    private val botUsers = ConcurrentHashMap<Long, MockBotUser>()
    
    data class MockBotUser(
        val id: Long,
        val isBot: Boolean,
        val firstName: String,
        val lastName: String? = null,
        val username: String? = null,
        val languageCode: String? = null
    )
    
    data class MockTelegramResponse<T>(
        val ok: Boolean,
        val result: T? = null,
        val description: String? = null,
        val errorCode: Int? = null
    )
    
    /**
     * Register a mock bot user for testing
     */
    fun registerMockBot(
        id: Long,
        firstName: String,
        username: String? = null,
        isBot: Boolean = true,
        lastName: String? = null,
        languageCode: String? = null
    ): MockBotUser {
        val mockBot = MockBotUser(
            id = id,
            isBot = isBot,
            firstName = firstName,
            lastName = lastName,
            username = username,
            languageCode = languageCode
        )
        botUsers[id] = mockBot
        return mockBot
    }
    
    /**
     * Get a mock bot user by ID
     */
    fun getMockBot(id: Long): MockBotUser? = botUsers[id]
    
    /**
     * Clear all mock bot users
     */
    fun clearMockBots() {
        botUsers.clear()
    }
    
    /**
     * Simulate getMe API response
     */
    fun simulateGetMeResponse(botId: Long): MockTelegramResponse<MockBotUser> {
        val bot = botUsers[botId]
        return if (bot != null) {
            MockTelegramResponse(
                ok = true,
                result = bot
            )
        } else {
            MockTelegramResponse(
                ok = false,
                description = "Bot not found",
                errorCode = 404
            )
        }
    }
    
    /**
     * Simulate an error response
     */
    fun simulateErrorResponse(errorCode: Int, description: String): MockTelegramResponse<Nothing> {
        return MockTelegramResponse(
            ok = false,
            description = description,
            errorCode = errorCode
        )
    }
}

@TestConfiguration
class MockTelegramBotConfiguration {
    
    @Bean
    @Primary
    fun mockTelegramBotServer(): MockTelegramBotServer {
        return MockTelegramBotServer()
    }
}
