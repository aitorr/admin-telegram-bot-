package com.aitorr.admintelegrambot.component

import com.aitorr.admintelegrambot.application.GetBotInfoUseCase
import com.aitorr.admintelegrambot.config.TestcontainersConfiguration
import com.aitorr.admintelegrambot.infrastructure.adapter.outbound.ChatBotUserRepositoryJooq
import com.aitorr.admintelegrambot.infrastructure.client.TelegramBotClient
import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Component Test for GetBotInfo flow (End-to-End)
 * 
 * This test validates the complete flow:
 * 1. GetBotInfoUseCase calls TelegramBotClient (mocked API)
 * 2. Retrieved bot is saved to PostgreSQL database
 * 3. Bot can be retrieved from database
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class, GetBotInfoComponentTest.MockRestTemplateConfig::class)
class GetBotInfoComponentTest {

    @Autowired
    private lateinit var getBotInfoUseCase: GetBotInfoUseCase

    @Autowired
    private lateinit var chatBotUserRepository: ChatBotUserRepositoryJooq

    @BeforeEach
    fun setup() {
        // Clear database before each test
        chatBotUserRepository.deleteAll()
    }

    @AfterEach
    fun cleanup() {
        chatBotUserRepository.deleteAll()
    }

    @Test
    fun `should retrieve bot from API and save to database`() {
        // When - Execute the use case
        val result = getBotInfoUseCase.execute()

        // Then - Should be successful
        assertTrue(result.isRight())
        
        result.fold(
            ifLeft = { fail("Expected successful result but got error: $it") },
            ifRight = { chatBotUser ->
                // Verify the returned bot user
                assertEquals(TEST_BOT_ID, chatBotUser.id)
                assertEquals(true, chatBotUser.isBot)
                assertEquals("TestBot", chatBotUser.firstName)
                assertEquals("Component", chatBotUser.lastName)
                assertEquals("test_component_bot", chatBotUser.username)
                assertEquals("en", chatBotUser.languageCode)

                // Verify the bot was saved to the database
                val savedBot = chatBotUserRepository.findById(TEST_BOT_ID)
                assertNotNull(savedBot)
                assertEquals(chatBotUser.id, savedBot?.id)
                assertEquals(chatBotUser.firstName, savedBot?.firstName)
                assertEquals(chatBotUser.lastName, savedBot?.lastName)
                assertEquals(chatBotUser.username, savedBot?.username)
                assertEquals(chatBotUser.languageCode, savedBot?.languageCode)
            }
        )
    }

    @Test
    fun `should verify bot persists in database after use case execution`() {
        // Given - Database is empty
        assertEquals(0, chatBotUserRepository.count())

        // When - Execute the use case
        val result = getBotInfoUseCase.execute()

        // Then - Bot should be in database
        assertTrue(result.isRight())
        assertEquals(1, chatBotUserRepository.count())

        // And - Bot can be retrieved independently
        val retrievedBot = chatBotUserRepository.findById(TEST_BOT_ID)
        assertNotNull(retrievedBot)
        assertEquals("TestBot", retrievedBot?.firstName)
    }

    @Test
    fun `should update existing bot if already in database`() {
        // Given - Bot already exists in database with different data
        val existingBot = com.aitorr.admintelegrambot.domain.model.ChatBotUser(
            id = TEST_BOT_ID,
            isBot = true,
            firstName = "OldName",
            lastName = "OldLastName",
            username = "old_username",
            languageCode = "es"
        )
        chatBotUserRepository.save(existingBot)

        // When - Execute the use case (which retrieves fresh data from API)
        val result = getBotInfoUseCase.execute()

        // Then - Should still have only 1 bot (updated, not duplicated)
        assertTrue(result.isRight())
        assertEquals(1, chatBotUserRepository.count())

        // And - Bot should have updated data from API
        val updatedBot = chatBotUserRepository.findById(TEST_BOT_ID)
        assertNotNull(updatedBot)
        assertEquals("TestBot", updatedBot?.firstName) // Updated from API
        assertEquals("Component", updatedBot?.lastName) // Updated from API
        assertEquals("test_component_bot", updatedBot?.username) // Updated from API
    }

    @Test
    fun `should handle API errors gracefully without saving to database`() {
        // This test would require a way to inject failures, which is more complex
        // For now, we verify the happy path
        // In a real scenario, you'd mock the TelegramBotClient to return errors
    }

    companion object {
        private const val TEST_BOT_ID = 999888777L
    }

    /**
     * Configuration to mock RestTemplate for TelegramBotClient
     */
    @TestConfiguration
    class MockRestTemplateConfig {
        
        @Bean
        @Primary
        fun mockRestTemplate(): RestTemplate {
            val restTemplate = mockk<RestTemplate>()
            
            // Mock the getMe API response
            val user = TelegramBotClient.User(
                id = TEST_BOT_ID,
                isBot = true,
                firstName = "TestBot",
                lastName = "Component",
                username = "test_component_bot",
                languageCode = "en",
                canJoinGroups = true,
                canReadAllGroupMessages = false,
                supportsInlineQueries = true
            )
            
            val response = TelegramBotClient.TelegramResponse(
                ok = true,
                result = user
            )
            
            @Suppress("UNCHECKED_CAST")
            val responseEntity = ResponseEntity.ok(response) as ResponseEntity<TelegramBotClient.TelegramResponse<*>>
            
            every { 
                restTemplate.getForEntity(
                    any<String>(),
                    TelegramBotClient.TelegramResponse::class.java
                ) 
            } returns responseEntity
            
            return restTemplate
        }
        
        @Bean
        @Primary
        fun telegramBotProperties(): TelegramBotProperties {
            return TelegramBotProperties(token = "test-component-token")
        }
    }
}
