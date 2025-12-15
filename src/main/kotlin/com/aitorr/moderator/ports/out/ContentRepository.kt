package com.aitorr.moderator.ports.out

import com.aitorr.moderator.domain.ModerationResult

interface ContentRepository {
    fun saveResult(result: ModerationResult)
    // Additional methods: fetch, update, findById, etc.
}
