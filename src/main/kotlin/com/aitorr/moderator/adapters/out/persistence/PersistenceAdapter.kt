package com.aitorr.moderator.adapters.out.persistence

import com.aitorr.moderator.domain.ModerationResult
import com.aitorr.moderator.ports.out.ContentRepository
import org.springframework.stereotype.Component

@Component
class PersistenceAdapter(
    private val jpa: JpaModerationRepository
) : ContentRepository {

    override fun saveResult(result: ModerationResult) {
        val entity = ModerationEntity(requestId = result.requestId, decision = result.decision, reason = result.reason)
        jpa.save(entity)
    }
}
