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
