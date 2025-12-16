package com.aitorr.admintelegrambot.domain.model

data class TelegramResponse<T>(
    val ok: Boolean,
    val result: T? = null,
    val description: String? = null,
    val errorCode: Int? = null
)
