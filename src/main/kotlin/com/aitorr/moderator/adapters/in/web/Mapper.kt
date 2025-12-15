package com.aitorr.moderator.adapters.`in`.web

import com.aitorr.moderator.adapters.`in`.web.dto.ModerationRequestDto
import com.aitorr.moderator.adapters.`in`.web.dto.ModerationResponseDto
import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationResult

fun ModerationRequestDto.toDomain(): ModerationRequest =
    ModerationRequest(id = id, content = content, authorId = authorId)

fun ModerationResult.toDto(): ModerationResponseDto =
    ModerationResponseDto(requestId = requestId, decision = decision.name, reason = reason)
