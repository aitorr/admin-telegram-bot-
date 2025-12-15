package com.aitorr.moderator.adapters.`in`.web

import com.aitorr.moderator.adapters.`in`.web.dto.ModerationRequestDto
import com.aitorr.moderator.ports.`in`.ModerateContentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/moderation")
class ModerationController(
    private val moderateContent: ModerateContentUseCase
) {

    @PostMapping
    fun moderate(@RequestBody requestDto: ModerationRequestDto): ResponseEntity<Any> {
        val domainReq = requestDto.toDomain()
        val result = moderateContent.moderate(domainReq)
        return ResponseEntity.ok(result.toDto())
    }
}
