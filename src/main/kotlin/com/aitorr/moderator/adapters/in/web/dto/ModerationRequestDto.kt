package com.aitorr.moderator.adapters.`in`.web.dto

data class ModerationRequestDto(
    val id: String,
    val content: String,
    val authorId: String? = null
)
