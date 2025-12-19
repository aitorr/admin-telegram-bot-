# Admin Telegram Bot

A Spring Boot application written in Kotlin following hexagonal architecture pattern.

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/aitorr/admintelegrambot/
│   │   ├── AdminTelegramBotApplication.kt      # Main entry point
│   │   ├── application/                         # Use cases and application services
│   │   ├── domain/                              # Business logic and domain models
│   │   └── infrastructure/                      # Adapters and external integrations
│   └── resources/
│       └── application.properties
└── test/
    └── kotlin/com/aitorr/admintelegrambot/
        └── AdminTelegramBotApplicationTests.kt
```

## Building the Project

```bash
./gradlew build
```

## Running Tests

```bash
./gradlew test
```

## Running the Application

```bash
./gradlew bootRun
```

## Technology Stack

- Spring Boot 3.2.0
- Kotlin 1.9.20
- Java 17
- Gradle 8.5
