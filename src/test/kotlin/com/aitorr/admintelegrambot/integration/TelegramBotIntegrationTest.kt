package com.aitorr.admintelegrambot.integration

import com.aitorr.admintelegrambot.config.MockTelegramBotServer
import com.aitorr.admintelegrambot.config.TestcontainersConfiguration
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.infrastructure.persistence.ChatBotUserEntity
import com.aitorr.admintelegrambot.infrastructure.persistence.ChatBotUserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Integration test using Testcontainers with PostgreSQL and mock Telegram Bot
 * 
 * This test demonstrates:
 * 1. Using Testcontainers to run PostgreSQL in Docker
 * 2. Testing JPA repositories with a real database
 * 3. Using a mock Telegram bot server to simulate API calls
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
class TelegramBotIntegrationTest {

    @Autowired
    private lateinit var chatBotUserRepository: ChatBotUserRepository

    @Autowired
    private lateinit var mockTelegramBotServer: MockTelegramBotServer

    @BeforeEach
    fun setup() {
        // Clear database before each test
        chatBotUserRepository.deleteAll()
        // Clear mock bots
        mockTelegramBotServer.clearMockBots()
    }

    @AfterEach
    fun cleanup() {
        chatBotUserRepository.deleteAll()
        mockTelegramBotServer.clearMockBots()
    }

    @Test
    fun `should save and retrieve bot user from PostgreSQL database`() {
        // Given
        val botUser = ChatBotUserEntity(
            id = 123456789L,
            isBot = true,
            firstName = "TestBot",
            lastName = "Tester",
            username = "test_bot",
            languageCode = "en"
        )

        // When
        val savedBot = chatBotUserRepository.save(botUser)
        val retrievedBot = chatBotUserRepository.findById(savedBot.id)

        // Then
        assertTrue(retrievedBot.isPresent)
        assertEquals(botUser.id, retrievedBot.get().id)
        assertEquals(botUser.firstName, retrievedBot.get().firstName)
        assertEquals(botUser.username, retrievedBot.get().username)
        assertEquals(botUser.languageCode, retrievedBot.get().languageCode)
    }

    @Test
    fun `should persist multiple bot users in PostgreSQL`() {
        // Given
        val bot1 = ChatBotUserEntity(
            id = 111111111L,
            isBot = true,
            firstName = "Bot1",
            username = "bot_one"
        )
        val bot2 = ChatBotUserEntity(
            id = 222222222L,
            isBot = true,
            firstName = "Bot2",
            username = "bot_two"
        )

        // When
        chatBotUserRepository.saveAll(listOf(bot1, bot2))
        val allBots = chatBotUserRepository.findAll()

        // Then
        assertEquals(2, allBots.size)
        assertTrue(allBots.any { it.id == bot1.id })
        assertTrue(allBots.any { it.id == bot2.id })
    }

    @Test
    fun `should update existing bot user in database`() {
        // Given
        val originalBot = ChatBotUserEntity(
            id = 333333333L,
            isBot = true,
            firstName = "OriginalName",
            username = "original_bot"
        )
        chatBotUserRepository.save(originalBot)

        // When
        originalBot.firstName = "UpdatedName"
        chatBotUserRepository.save(originalBot)
        val retrieved = chatBotUserRepository.findById(originalBot.id)

        // Then
        assertTrue(retrieved.isPresent)
        assertEquals("UpdatedName", retrieved.get().firstName)
        assertEquals("original_bot", retrieved.get().username)
    }

    @Test
    fun `should delete bot user from database`() {
        // Given
        val botUser = ChatBotUserEntity(
            id = 444444444L,
            isBot = true,
            firstName = "ToBeDeleted",
            username = "delete_me"
        )
        chatBotUserRepository.save(botUser)

        // When
        chatBotUserRepository.deleteById(botUser.id)
        val retrieved = chatBotUserRepository.findById(botUser.id)

        // Then
        assertFalse(retrieved.isPresent)
    }

    @Test
    fun `mock telegram bot server should register and retrieve bot`() {
        // Given
        val botId = 555555555L

        // When
        val registeredBot = mockTelegramBotServer.registerMockBot(
            id = botId,
            firstName = "MockBot",
            username = "mock_bot",
            languageCode = "es"
        )

        // Then
        assertNotNull(registeredBot)
        assertEquals(botId, registeredBot.id)
        assertEquals("MockBot", registeredBot.firstName)
        assertEquals("mock_bot", registeredBot.username)

        val retrievedBot = mockTelegramBotServer.getMockBot(botId)
        assertNotNull(retrievedBot)
        assertEquals(registeredBot.id, retrievedBot?.id)
    }

    @Test
    fun `mock telegram bot server should simulate getMe response`() {
        // Given
        val botId = 666666666L
        mockTelegramBotServer.registerMockBot(
            id = botId,
            firstName = "GetMeBot",
            username = "getme_bot"
        )

        // When
        val response = mockTelegramBotServer.simulateGetMeResponse(botId)

        // Then
        assertTrue(response.ok)
        assertNotNull(response.result)
        assertEquals(botId, response.result?.id)
        assertEquals("GetMeBot", response.result?.firstName)
        assertEquals("getme_bot", response.result?.username)
    }

    @Test
    fun `mock telegram bot server should return 404 for unknown bot`() {
        // Given
        val unknownBotId = 999999999L

        // When
        val response = mockTelegramBotServer.simulateGetMeResponse(unknownBotId)

        // Then
        assertFalse(response.ok)
        assertEquals(404, response.errorCode)
        assertEquals("Bot not found", response.description)
        assertNull(response.result)
    }

    @Test
    fun `mock telegram bot server should simulate error responses`() {
        // When
        val errorResponse = mockTelegramBotServer.simulateErrorResponse(
            errorCode = 401,
            description = "Unauthorized"
        )

        // Then
        assertFalse(errorResponse.ok)
        assertEquals(401, errorResponse.errorCode)
        assertEquals("Unauthorized", errorResponse.description)
    }

    @Test
    fun `should convert between domain model and entity`() {
        // Given
        val domainModel = ChatBotUser(
            id = 777777777L,
            isBot = true,
            firstName = "DomainBot",
            lastName = "Model",
            username = "domain_bot",
            languageCode = "fr"
        )

        // When - Convert to entity
        val entity = ChatBotUserEntity.fromDomain(domainModel)
        
        // Then - Verify entity
        assertEquals(domainModel.id, entity.id)
        assertEquals(domainModel.firstName, entity.firstName)
        assertEquals(domainModel.username, entity.username)

        // When - Convert back to domain
        val convertedDomain = entity.toDomain()

        // Then - Verify round-trip conversion
        assertEquals(domainModel.id, convertedDomain.id)
        assertEquals(domainModel.firstName, convertedDomain.firstName)
        assertEquals(domainModel.lastName, convertedDomain.lastName)
        assertEquals(domainModel.username, convertedDomain.username)
        assertEquals(domainModel.languageCode, convertedDomain.languageCode)
    }

    @Test
    fun `integration test - save mock bot to database and verify persistence`() {
        // Given - Register a bot in the mock server
        val botId = 888888888L
        val mockBot = mockTelegramBotServer.registerMockBot(
            id = botId,
            firstName = "IntegrationBot",
            username = "integration_bot",
            languageCode = "de"
        )

        // When - Simulate getting bot info from Telegram API
        val apiResponse = mockTelegramBotServer.simulateGetMeResponse(botId)
        
        // Then - API response should be successful
        assertTrue(apiResponse.ok)
        assertNotNull(apiResponse.result)
        
        val resultBot = apiResponse.result!!

        // When - Save to database
        val entity = ChatBotUserEntity(
            id = resultBot.id,
            isBot = resultBot.isBot,
            firstName = resultBot.firstName,
            lastName = resultBot.lastName,
            username = resultBot.username,
            languageCode = resultBot.languageCode
        )
        chatBotUserRepository.save(entity)

        // Then - Verify persistence
        val persistedBot = chatBotUserRepository.findById(botId)
        assertTrue(persistedBot.isPresent)
        assertEquals(mockBot.firstName, persistedBot.get().firstName)
        assertEquals(mockBot.username, persistedBot.get().username)
        assertEquals(mockBot.languageCode, persistedBot.get().languageCode)
    }
}
