package com.aitorr.moderator.adapters.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import com.aitorr.moderator.domain.ModerationDecision

@Entity
@Table(name = "moderation_results")
data class ModerationEntity(
    @Id
    @Column(nullable = false)
    val requestId: String,
    @Column(nullable = false)
    val decision: String,
    @Column
    val reason: String?
) {
    constructor(requestId: String, decision: ModerationDecision, reason: String?) :
        this(requestId, decision.name, reason)
}
