# Admin Telegram Bot

A Spring Boot application following hexagonal architecture pattern.

## Project Structure

```
src/
├── main/
│   ├── java/com/aitorr/admintelegrambot/
│   │   ├── AdminTelegramBotApplication.java    # Main entry point
│   │   ├── application/                         # Use cases and application services
│   │   ├── domain/                              # Business logic and domain models
│   │   └── infrastructure/                      # Adapters and external integrations
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/aitorr/admintelegrambot/
        └── AdminTelegramBotApplicationTests.java
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
- Java 17
- Gradle 8.5
