package com.aitorr.moderator.domain

data class ModerationRequest(
    val id: String,
    val content: String,
    val authorId: String? = null
)
