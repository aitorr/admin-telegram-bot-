package com.aitorr.moderator.application

import com.aitorr.moderator.domain.ModerationDecision
import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationResult
import com.aitorr.moderator.ports.`in`.ModerateContentUseCase
import com.aitorr.moderator.ports.out.ContentRepository
import org.springframework.stereotype.Service

@Service
class ModerateContentService(
    private val contentRepository: ContentRepository
) : ModerateContentUseCase {

    override fun moderate(request: ModerationRequest): ModerationResult {
        // Placeholder simple rule-based logic (expand with ML or rules)
        val decision = when {
            request.content.isBlank() -> ModerationDecision.HOLD
            request.content.length < 5 -> ModerationDecision.REJECT
            request.content.contains("spam", ignoreCase = true) -> ModerationDecision.REJECT
            else -> ModerationDecision.APPROVE
        }
        val result = ModerationResult(requestId = request.id, decision = decision, reason = null)
        contentRepository.saveResult(result)
        return result
    }
}
