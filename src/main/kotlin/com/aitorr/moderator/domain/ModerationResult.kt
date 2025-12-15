package com.aitorr.moderator.domain

enum class ModerationDecision {
    APPROVE,
    REJECT,
    HOLD
}

data class ModerationResult(
    val requestId: String,
    val decision: ModerationDecision,
    val reason: String? = null
)
