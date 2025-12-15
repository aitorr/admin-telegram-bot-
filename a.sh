#!/usr/bin/env bash
# push_scaffold.sh
# Creates/overwrites local branch, writes a Spring Boot + Kotlin hexagonal scaffold,
# commits and force-pushes it to origin using --force-with-lease.
#
# Usage:
#   ./push_scaffold.sh                     # uses default branch feature/initial-hexagonal-structure
#   ./push_scaffold.sh my-branch           # use explicit branch
#   BRANCH=other ./push_scaffold.sh -y     # non-interactive, use env BRANCH or arg
#
# WARNING: This will overwrite origin/<branch> when pushed. It uses --force-with-lease
# to reduce accidental clobbering, but it is still destructive. Use with care.

set -euo pipefail

DEFAULT_BRANCH="feature/initial-hexagonal-structure"
BRANCH="${1:-${BRANCH:-$DEFAULT_BRANCH}}"
COMMIT_MSG="feat: initial hexagonal Spring Boot Kotlin project scaffold"
NONINTERACTIVE=false

# parse -y for yes
if [[ "${1:-}" == "-y" || "${2:-}" == "-y" || "${NONINTERACTIVE:-}" == "true" ]]; then
  NONINTERACTIVE=true
fi
# Also allow env YES or CI to auto-accept
if [[ "${YES:-}" == "1" || "${CI:-}" == "true" ]]; then
  NONINTERACTIVE=true
fi

echo "Target branch: $BRANCH"
echo "Commit message: $COMMIT_MSG"

if ! $NONINTERACTIVE; then
  read -r -p "This will create/reset local branch '$BRANCH', write scaffold files, commit, and force-push to origin/$BRANCH (using --force-with-lease). Continue? [y/N] " confirm
  if [[ "${confirm,,}" != "y" ]]; then
    echo "Aborted."
    exit 1
  fi
fi

# Ensure we are in a git repository
if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Not inside a git repository. cd into the repo root and run again."
  exit 1
fi

# Fetch remote refs (best-effort)
git fetch origin --prune || true

# Create/reset local branch to the remote branch if it exists, else create from current HEAD
if git ls-remote --exit-code --heads origin "refs/heads/$BRANCH" >/dev/null 2>&1; then
  echo "Remote branch origin/$BRANCH found — creating local branch tracking remote."
  git checkout -B "$BRANCH" "origin/$BRANCH"
else
  echo "Remote branch origin/$BRANCH not found — creating local branch from current HEAD."
  git checkout -B "$BRANCH"
fi

# Create directories
mkdir -p src/main/kotlin/com/aitorr/moderator/adapters/in/web/dto
mkdir -p src/main/kotlin/com/aitorr/moderator/adapters/in/web
mkdir -p src/main/kotlin/com/aitorr/moderator/adapters/out/persistence
mkdir -p src/main/kotlin/com/aitorr/moderator/application
mkdir -p src/main/kotlin/com/aitorr/moderator/domain
mkdir -p src/main/kotlin/com/aitorr/moderator/ports/in
mkdir -p src/main/kotlin/com/aitorr/moderator/ports/out
mkdir -p src/main/kotlin/com/aitorr/moderator/config
mkdir -p src/main/resources
mkdir -p src/test/kotlin/com/aitorr/moderator/application

# Write files (overwrite if exist)

cat > settings.gradle.kts <<'EOF'
rootProject.name = "moderator"
EOF

cat > gradle.properties <<'EOF'
kotlin.version=1.9.22
springBoot.version=3.2.6
java.version=17
EOF

cat > build.gradle.kts <<'EOF'
plugins {
    kotlin("jvm") version property("kotlin.version") as String
    id("org.springframework.boot") version property("springBoot.version") as String
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version property("kotlin.version") as String
    kotlin("plugin.jpa") version property("kotlin.version") as String
}

group = "com.aitorr"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.toVersion(property("java.version") as String)

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Optional: PostgreSQL driver (change if needed)
    runtimeOnly("org.postgresql:postgresql:42.6.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
EOF

cat > .gitignore <<'EOF'
# Gradle
.gradle/
build/

# IntelliJ
.idea/
*.iml

# OS
.DS_Store

# Logs
*.log
EOF

cat > README.md <<'EOF'
# Moderator (hexagonal) — Kotlin + Spring Boot

This is a starter project for a moderator service using a hexagonal (ports & adapters) architecture.

Structure highlights
- com.aitorr.moderator.domain — core domain models and business rules
- com.aitorr.moderator.ports — inbound and outbound ports (interfaces)
- com.aitorr.moderator.application — use case implementations
- com.aitorr.moderator.adapters.in.web — RestController, DTOs
- com.aitorr.moderator.adapters.out.persistence — JPA entities & repositories

How to run
1. Update `application.yml` with your DB settings (or run with an H2 profile).
2. ./gradlew bootRun

Notes
- Change the group/package if you want another base package.
- Add more adapters (e.g., messaging) as needed.
EOF

cat > src/main/resources/application.yml <<'EOF'
spring:
  datasource:
    # replace these with your DB credentials or configure profile-specific yml
    url: jdbc:postgresql://localhost:5432/moderator
    username: moderator
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 8080
EOF

cat > src/main/kotlin/com/aitorr/moderator/ModeratorApplication.kt <<'EOF'
package com.aitorr.moderator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ModeratorApplication

fun main(args: Array<String>) {
    runApplication<ModeratorApplication>(*args)
}
EOF

cat > src/main/kotlin/com/aitorr/moderator/domain/ModerationRequest.kt <<'EOF'
package com.aitorr.moderator.domain

data class ModerationRequest(
    val id: String,
    val content: String,
    val authorId: String? = null
)
EOF

cat > src/main/kotlin/com/aitorr/moderator/domain/ModerationResult.kt <<'EOF'
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
EOF

cat > src/main/kotlin/com/aitorr/moderator/ports/in/ModerateContentUseCase.kt <<'EOF'
package com.aitorr.moderator.ports.`in`

import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationResult

interface ModerateContentUseCase {
    fun moderate(request: ModerationRequest): ModerationResult
}
EOF

cat > src/main/kotlin/com/aitorr/moderator/ports/out/ContentRepository.kt <<'EOF'
package com.aitorr.moderator.ports.out

import com.aitorr.moderator.domain.ModerationResult

interface ContentRepository {
    fun saveResult(result: ModerationResult)
    // Additional methods: fetch, update, findById, etc.
}
EOF

cat > src/main/kotlin/com/aitorr/moderator/application/ModerateContentService.kt <<'EOF'
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
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/in/web/dto/ModerationRequestDto.kt <<'EOF'
package com.aitorr.moderator.adapters.`in`.web.dto

data class ModerationRequestDto(
    val id: String,
    val content: String,
    val authorId: String? = null
)
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/in/web/dto/ModerationResponseDto.kt <<'EOF'
package com.aitorr.moderator.adapters.`in`.web.dto

data class ModerationResponseDto(
    val requestId: String,
    val decision: String,
    val reason: String? = null
)
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/in/web/Mapper.kt <<'EOF'
package com.aitorr.moderator.adapters.`in`.web

import com.aitorr.moderator.adapters.`in`.web.dto.ModerationRequestDto
import com.aitorr.moderator.adapters.`in`.web.dto.ModerationResponseDto
import com.aitorr.moderator.domain.ModerationRequest
import com.aitorr.moderator.domain.ModerationResult

fun ModerationRequestDto.toDomain(): ModerationRequest =
    ModerationRequest(id = id, content = content, authorId = authorId)

fun ModerationResult.toDto(): ModerationResponseDto =
    ModerationResponseDto(requestId = requestId, decision = decision.name, reason = reason)
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/in/web/ModerationController.kt <<'EOF'
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
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/out/persistence/ModerationEntity.kt <<'EOF'
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
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/out/persistence/JpaModerationRepository.kt <<'EOF'
package com.aitorr.moderator.adapters.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaModerationRepository : JpaRepository<ModerationEntity, String>
EOF

cat > src/main/kotlin/com/aitorr/moderator/adapters/out/persistence/PersistenceAdapter.kt <<'EOF'
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
EOF

cat > src/main/kotlin/com/aitorr/moderator/config/BeansConfig.kt <<'EOF'
package com.aitorr.moderator.config

import org.springframework.context.annotation.Configuration

@Configuration
class BeansConfig {
    // If you need special beans or to override adapters for tests, configure them here.
}
EOF

cat > src/test/kotlin/com/aitorr/moderator/application/ModerateContentServiceTest.kt <<'EOF'
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
EOF

# Ensure the script itself is executable (useful if re-committed)
chmod +x push_scaffold.sh || true

# Stage files
git add .

# Mark script executable in index so the executable bit is preserved in the commit
git update-index --chmod=+x push_scaffold.sh || true

# Commit if there are staged changes
if git diff --cached --quiet; then
  echo "No staged changes to commit."
else
  # configure minimal committer if not set
  if ! git config user.name >/dev/null 2>&1 || [[ -z "$(git config user.name || true)" ]]; then
    git config user.name "scaffold-bot"
  fi
  if ! git config user.email >/dev/null 2>&1 || [[ -z "$(git config user.email || true)" ]]; then
    git config user.email "scaffold-bot@example.com"
  fi

  git commit -m "$COMMIT_MSG"
fi

echo "Pushing branch '$BRANCH' to origin with --force-with-lease..."
git push --force-with-lease origin "HEAD:refs/heads/$BRANCH"

echo "Done. Branch '$BRANCH' has been pushed to origin."
echo "Open: https://github.com/aitorr/admin-telegram-bot-/tree/$BRANCH"