package com.aitorr.moderator.application

import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationDecision
import com.aitorr.moderator.ports.out.ContentRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryRepo : ContentRepository {
    val store = mutableListOf<com.aitorr.moderator.domain.ModerationResult>()
    override fun saveResult(result: com.aitorr.moderator.domain.ModerationResult) {
        store.add(result)
    }
}

class ModerateContentServiceTest {

    @Test
    fun `short content should be rejected`() {
        val repo = InMemoryRepo()
        val service = ModerateContentService(repo)
        val request = ModerationRequest(id = "1", content = "hi")
        val result = service.moderate(request)
        assertEquals(ModerationDecision.REJECT, result.decision)
        assertEquals(1, repo.store.size)
    }
}
