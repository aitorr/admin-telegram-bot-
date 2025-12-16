package com.aitorr.admintelegrambot.application

import arrow.core.left
import arrow.core.right
import com.aitorr.admintelegrambot.application.GetBotInfoUseCase.GetBotInfoUseCaseError
import com.aitorr.admintelegrambot.domain.model.ChatBotUser
import com.aitorr.admintelegrambot.domain.port.GetChatBot
import com.aitorr.admintelegrambot.domain.port.GetChatBot.GetChatBotError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetBotInfoUseCaseTest {

    private val getChatBot = mockk<GetChatBot>()
    private val useCase = GetBotInfoUseCase(getChatBot)

    @Test
    fun `execute should return ChatBotUser when port returns success`() {
        val expectedUser = ChatBotUser(
            id = 123L,
            isBot = true,
            firstName = "TestBot",
            lastName = "Bot",
            username = "test_bot",
            languageCode = "en"
        )
        every { getChatBot.getChatBot() } returns expectedUser.right()

        val result = useCase.execute()

        assertTrue(result.isRight())
        result.fold(
            ifLeft = { fail("Expected Right but got Left: $it") },
            ifRight = { user ->
                assertEquals(expectedUser.id, user.id)
                assertEquals(expectedUser.firstName, user.firstName)
                assertEquals(expectedUser.username, user.username)
            }
        )
        verify(exactly = 1) { getChatBot.getChatBot() }
    }

    @Test
    fun `execute should return ChatBotDoesNotExistError when port returns ChatBotNotFoundError`() {
        val portError = GetChatBotError.ChatBotNotFoundError(
            message = "Bot not found in API"
        )
        every { getChatBot.getChatBot() } returns portError.left()

        val result = useCase.execute()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetBotInfoUseCaseError.ChatBotDoesNotExistError)
                assertEquals("Chat bot does not exist", error.message)
                assertNotNull(error.sourceError)
                assertEquals("Bot not found in API", error.sourceError?.message)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
        verify(exactly = 1) { getChatBot.getChatBot() }
    }

    @Test
    fun `execute should return UnexpectedUseCaseError when port returns TechnicalError`() {
        val portError = GetChatBotError.TechnicalError(
            message = "Network timeout",
            errorCode = 504
        )
        every { getChatBot.getChatBot() } returns portError.left()

        val result = useCase.execute()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetBotInfoUseCaseError.UnexpectedUseCaseError)
                assertEquals("Unexpected error retrieving bot info", error.message)
                assertNotNull(error.sourceError)
                assertEquals("Network timeout", error.sourceError?.message)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
        verify(exactly = 1) { getChatBot.getChatBot() }
    }

    @Test
    fun `execute should return UnexpectedUseCaseError when port returns UnexpectedError`() {
        val portError = GetChatBotError.UnexpectedError(
            message = "Unexpected exception occurred",
            cause = RuntimeException("Something went wrong")
        )
        every { getChatBot.getChatBot() } returns portError.left()

        val result = useCase.execute()

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue(error is GetBotInfoUseCaseError.UnexpectedUseCaseError)
                assertEquals("Unexpected error retrieving bot info", error.message)
                assertNotNull(error.sourceError)
                assertEquals("Unexpected exception occurred", error.sourceError?.message)
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
        verify(exactly = 1) { getChatBot.getChatBot() }
    }

    @Test
    fun `error trace should show chained errors`() {
        val portError = GetChatBotError.TechnicalError(
            message = "API connection failed",
            errorCode = 503
        )
        every { getChatBot.getChatBot() } returns portError.left()

        val result = useCase.execute()

        result.fold(
            ifLeft = { error ->
                val errorTrace = error.toErrorTrace()
                assertTrue(errorTrace.contains("UnexpectedUseCaseError: Unexpected error retrieving bot info"))
                assertTrue(errorTrace.contains("TechnicalError: API connection failed"))
                assertTrue(errorTrace.contains("Caused by:"))
            },
            ifRight = { fail("Expected Left but got Right: $it") }
        )
    }
}
