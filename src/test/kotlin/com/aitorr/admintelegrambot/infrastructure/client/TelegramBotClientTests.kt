package com.aitorr.admintelegrambot.infrastructure.client

import com.aitorr.admintelegrambot.domain.port.GetChatBotError
import com.aitorr.admintelegrambot.infrastructure.config.TelegramBotProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

class TelegramBotClientTests {

    private lateinit var restTemplate: RestTemplate
    private lateinit var telegramBotProperties: TelegramBotProperties
    private lateinit var telegramBotClient: TelegramBotClient
    
    companion object {
        private const val TEST_TOKEN = "test-token-123"
        private const val BASE_URL = "https://api.telegram.org/bot$TEST_TOKEN/getMe"
    }

    @BeforeEach
    fun setup() {
        restTemplate = mockk()
        telegramBotProperties = TelegramBotProperties(token = TEST_TOKEN)
        telegramBotClient = TelegramBotClient(telegramBotProperties, restTemplate)
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <T> mockResponseEntity(response: TelegramBotClient.TelegramResponse<T>): ResponseEntity<TelegramBotClient.TelegramResponse<*>> {
        return ResponseEntity.ok(response) as ResponseEntity<TelegramBotClient.TelegramResponse<*>>
    }

    @Test
    fun `getChatBot should return ChatBotUser when API returns successful response`() {
        // Given
        val user = TelegramBotClient.User(
            id = 123456789L,
            isBot = true,
            firstName = "TestBot",
            lastName = "LastName",
            username = "test_bot",
            languageCode = "en",
            canJoinGroups = true,
            canReadAllGroupMessages = false,
            supportsInlineQueries = true
        )
        val response = TelegramBotClient.TelegramResponse(
            ok = true,
            result = user
        )
        val responseEntity = mockResponseEntity(response)

        every { 
            restTemplate.getForEntity(
                BASE_URL,
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } returns responseEntity

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isRight())
        result.fold(
            ifLeft = { fail("Expected Right but got Left: $it") },
            ifRight = { chatBotUser ->
                assertEquals(123456789L, chatBotUser.id)
                assertEquals(true, chatBotUser.isBot)
                assertEquals("TestBot", chatBotUser.firstName)
                assertEquals("LastName", chatBotUser.lastName)
                assertEquals("test_bot", chatBotUser.username)
                assertEquals("en", chatBotUser.languageCode)
            }
        )

        verify(exactly = 1) { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        }
    }

    @Test
    fun `getChatBot should return ChatBotNotFoundError when API returns 404 error code in response`() {
        // Given
        val response = TelegramBotClient.TelegramResponse<TelegramBotClient.User>(
            ok = false,
            result = null,
            description = "Bot not found",
            errorCode = 404
        )
        val responseEntity = mockResponseEntity(response)

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } returns responseEntity

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.ChatBotNotFoundError)
                assertEquals("Bot not found", error.message)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return TechnicalError when API returns non-404 error code`() {
        // Given
        val response = TelegramBotClient.TelegramResponse<TelegramBotClient.User>(
            ok = false,
            result = null,
            description = "Internal server error",
            errorCode = 500
        )
        val responseEntity = mockResponseEntity(response)

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } returns responseEntity

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.TechnicalError)
                assertEquals("Internal server error", error.message)
                assertEquals(500, (error as GetChatBotError.TechnicalError).errorCode)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return TechnicalError when API returns ok=false without error code`() {
        // Given
        val response = TelegramBotClient.TelegramResponse<TelegramBotClient.User>(
            ok = false,
            result = null,
            description = "Unknown error occurred",
            errorCode = null
        )
        val responseEntity = mockResponseEntity(response)

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } returns responseEntity

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.TechnicalError)
                assertEquals("Unknown error occurred", error.message)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return ChatBotNotFoundError when HTTP 404 exception is thrown`() {
        // Given
        val exception = HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found")

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } throws exception

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.ChatBotNotFoundError)
                assertTrue(error.message.contains("Chat bot not found"))
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return TechnicalError when HTTP 401 exception is thrown`() {
        // Given
        val exception = HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized")

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } throws exception

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.TechnicalError)
                assertTrue(error.message.contains("HTTP error"))
                assertEquals(401, (error as GetChatBotError.TechnicalError).errorCode)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return TechnicalError when HTTP 500 exception is thrown`() {
        // Given
        val exception = HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } throws exception

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.UnexpectedError)
                assertTrue(error.message.contains("Unexpected error calling Telegram API"))
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should return UnexpectedError when unexpected exception is thrown`() {
        // Given
        val exception = RuntimeException("Network timeout")

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } throws exception

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetChatBotError.UnexpectedError)
                assertTrue(error.message.contains("Unexpected error calling Telegram API"))
                assertTrue(error.message.contains("Network timeout"))
                assertNotNull((error as GetChatBotError.UnexpectedError).cause)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }

    @Test
    fun `getChatBot should handle response with minimal user data`() {
        // Given
        val user = TelegramBotClient.User(
            id = 987654321L,
            isBot = true,
            firstName = "MinimalBot",
            lastName = null,
            username = null,
            languageCode = null,
            canJoinGroups = null,
            canReadAllGroupMessages = null,
            supportsInlineQueries = null
        )
        val response = TelegramBotClient.TelegramResponse(
            ok = true,
            result = user
        )
        val responseEntity = mockResponseEntity(response)

        every { 
            restTemplate.getForEntity(
                any<String>(),
                TelegramBotClient.TelegramResponse::class.java
            ) 
        } returns responseEntity

        // When
        val result = telegramBotClient.getChatBot()

        // Then
        assertTrue(result.isRight())
        result.fold(
            ifLeft = { fail("Expected Right but got Left: $it") },
            ifRight = { chatBotUser ->
                assertEquals(987654321L, chatBotUser.id)
                assertEquals(true, chatBotUser.isBot)
                assertEquals("MinimalBot", chatBotUser.firstName)
                assertNull(chatBotUser.lastName)
                assertNull(chatBotUser.username)
                assertNull(chatBotUser.languageCode)
            }
        )
    }
}
