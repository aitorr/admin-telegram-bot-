package com.aitorr.moderator.adapters.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaModerationRepository : JpaRepository<ModerationEntity, String>
