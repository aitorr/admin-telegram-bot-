package com.aitorr.moderator.adapters.`in`.web.dto

data class ModerationResponseDto(
    val requestId: String,
    val decision: String,
    val reason: String? = null
)
