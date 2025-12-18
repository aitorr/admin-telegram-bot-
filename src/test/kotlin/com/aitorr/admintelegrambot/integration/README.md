# Integration Tests with Testcontainers

This directory contains integration tests that use **Testcontainers** to run a real PostgreSQL database in Docker and a **Mock Telegram Bot Server** to simulate Telegram API interactions.

## Features

### 🐳 Testcontainers with PostgreSQL
- Automatically spins up a PostgreSQL 15 container for each test run
- Tests run against a real database (not H2 or other in-memory databases)
- Database is automatically cleaned up after tests complete
- Container reuse is enabled to speed up consecutive test runs

### 🤖 Mock Telegram Bot Server
- Simulates Telegram Bot API without making real HTTP calls
- Register mock bots with custom properties
- Simulate successful API responses
- Simulate error responses (404, 401, 500, etc.)
- Perfect for testing bot behavior in isolation

## Prerequisites

- **Docker**: Must be running on your machine
- **Java 21**: Required by the project
- **Internet connection**: First run will download the PostgreSQL Docker image

## Running the Tests

```bash
# Run all integration tests
./gradlew test --tests "com.aitorr.admintelegrambot.integration.*"

# Run specific integration test
./gradlew test --tests "TelegramBotIntegrationTest"
```

## Test Structure

### `TelegramBotIntegrationTest.kt`
Comprehensive integration test covering:

1. **Database Operations**
   - Save and retrieve bot users from PostgreSQL
   - Update existing records
   - Delete records
   - Handle multiple entities

2. **Mock Bot Server**
   - Register mock bots
   - Simulate getMe API calls
   - Handle error responses
   - Test 404 scenarios

3. **Domain/Entity Conversion**
   - Convert between domain models and JPA entities
   - Round-trip conversion tests

4. **End-to-End Scenarios**
   - Full integration: Mock API → Database persistence → Retrieval

## Mock Telegram Bot Server Usage

```kotlin
// Register a mock bot
mockTelegramBotServer.registerMockBot(
    id = 123456789L,
    firstName = "TestBot",
    username = "test_bot",
    languageCode = "en"
)

// Simulate successful API response
val response = mockTelegramBotServer.simulateGetMeResponse(123456789L)

// Simulate error response
val errorResponse = mockTelegramBotServer.simulateErrorResponse(
    errorCode = 404,
    description = "Bot not found"
)
```

## Configuration

### Test Configuration
- `application-test.yml`: Test-specific Spring configuration
- `TestcontainersConfiguration.kt`: Testcontainers setup
- `MockTelegramBotConfiguration.kt`: Mock bot server configuration

### Container Reuse
To speed up test execution, container reuse is enabled in `.testcontainers.properties`.
The PostgreSQL container will be reused across test runs until you stop it manually or restart Docker.

## Troubleshooting

### Docker not running
```
Error: Could not find a valid Docker environment
```
**Solution**: Start Docker Desktop or Docker daemon

### Port already in use
```
Error: Bind for 0.0.0.0:5432 failed: port is already allocated
```
**Solution**: Testcontainers automatically finds available ports. If you see this error, stop any local PostgreSQL instances.

### Slow first run
The first test run will download the PostgreSQL Docker image (~80MB). Subsequent runs will be much faster due to container reuse.

## Benefits

✅ **Real Database Testing**: Test against actual PostgreSQL behavior, not in-memory databases  
✅ **Isolation**: Each test runs in a clean database state  
✅ **Fast Feedback**: Container reuse makes subsequent runs quick  
✅ **No External Dependencies**: Everything runs in Docker  
✅ **Mock API**: Test bot behavior without hitting real Telegram servers  
✅ **CI/CD Ready**: Works in any environment with Docker support
