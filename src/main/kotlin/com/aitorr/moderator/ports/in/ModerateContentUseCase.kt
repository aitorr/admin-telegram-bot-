package com.aitorr.moderator.ports.`in`

import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationResult

interface ModerateContentUseCase {
    fun moderate(request: ModerationRequest): ModerationResult
}
