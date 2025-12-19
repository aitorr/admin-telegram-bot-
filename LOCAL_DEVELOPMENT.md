# Admin Telegram Bot - Local Development Guide

A Spring Boot application written in Kotlin following hexagonal architecture pattern with comprehensive testing infrastructure using jOOQ, Flyway, and Testcontainers.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** - [Download from OpenJDK](https://adoptium.net/) or use SDKMAN:
  ```bash
  sdk install java 21.0.1-tem
  ```
- **Docker Desktop** - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
  - Make sure Docker Desktop is running before executing tests or running the application
- **Git** - For cloning the repository
- **IDE** (Optional but recommended) - IntelliJ IDEA, VS Code, or your preferred IDE

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/aitorr/admin-telegram-bot-.git
cd admin-telegram-bot-
```

### 2. Verify Java Version

```bash
java -version
# Should show Java 21
```

### 3. Start Docker Desktop

Ensure Docker Desktop is running. You can verify with:

```bash
docker --version
docker ps
```

### 4. Build the Project

```bash
./gradlew build
```

This will:
- Compile the Kotlin code
- Run all tests (unit, integration, and component tests)
- Generate the executable JAR file

**Note:** First build may take longer as it downloads dependencies and Docker images.

## 🧪 Running Tests

### Run All Tests (Requires Docker)

```bash
./gradlew test
```

This runs **30 tests** including:
- 9 unit tests (TelegramBotClientTests)
- 6 unit tests (GetBotInfoUseCaseTest)
- 15 integration tests (TelegramBotIntegrationTest)
- 3 component tests (GetBotInfoComponentTest)
- 1 context load test

### Run Only Unit Tests (No Docker Required)

```bash
./gradlew test --tests "*UseCaseTest" --tests "*ClientTests"
```

This runs **15 unit tests** that use mocks only.

### Run Specific Test Classes

```bash
# Run integration tests only
./gradlew test --tests "TelegramBotIntegrationTest"

# Run component tests only
./gradlew test --tests "GetBotInfoComponentTest"

# Run client tests only
./gradlew test --tests "TelegramBotClientTests"
```

## 🗄️ Database Setup

### Using Docker (Recommended for Local Development)

The project uses **PostgreSQL** via Docker. You have two options:

#### Option 1: Let Testcontainers Handle It (Automatic)

When running tests, Testcontainers automatically:
1. Pulls the PostgreSQL 15 Alpine image (if not already present)
2. Starts a PostgreSQL container
3. Runs Flyway migrations
4. Executes tests
5. Stops the container

**No manual setup required!**

#### Option 2: Run PostgreSQL Manually (For Running the Application)

If you want to run the application locally, start PostgreSQL manually:

```bash
docker run -d \
  --name admin-telegram-bot-postgres \
  -e POSTGRES_DB=admintelegrambot \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

Verify it's running:

```bash
docker ps | grep admin-telegram-bot-postgres
```

To stop the database:

```bash
docker stop admin-telegram-bot-postgres
```

To remove the container:

```bash
docker rm admin-telegram-bot-postgres
```

### Database Configuration

Default connection settings (can be overridden via environment variables):

- **URL:** `jdbc:postgresql://localhost:5432/admintelegrambot`
- **Username:** `postgres`
- **Password:** `postgres`

Override with environment variables:

```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/admintelegrambot"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="postgres"
```

## 🏃 Running the Application

### Using Gradle (Recommended)

```bash
./gradlew bootRun
```

### Using the JAR File

First build the project:

```bash
./gradlew build
```

Then run:

```bash
java -jar build/libs/admin-telegram-bot-0.0.1-SNAPSHOT.jar
```

### With Custom Configuration

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Or with environment variables:

```bash
export TELEGRAM_BOT_TOKEN="your-bot-token-here"
export DATABASE_URL="jdbc:postgresql://localhost:5432/admintelegrambot"
./gradlew bootRun
```

## 🔧 jOOQ Code Generation

The project uses jOOQ for type-safe database access. Code is generated from Flyway migrations.

### Generate jOOQ Code

```bash
./gradlew generateJooq
```

This will:
1. Start a PostgreSQL container via Testcontainers
2. Run Flyway migrations from `src/main/resources/db/migration/`
3. Generate jOOQ classes in `src/main/kotlin/com/aitorr/admintelegrambot/infrastructure/jooq/`
4. Stop the container

**Generated files include:**
- Tables (e.g., `ChatBotUsers`)
- Records (e.g., `ChatBotUsersRecord`)
- POJOs (e.g., `ChatBotUsers` POJO)
- DAOs (e.g., `ChatBotUsersDao`)

### When to Regenerate

Regenerate jOOQ code when you:
- Add a new Flyway migration
- Modify table schemas
- Change column types or constraints

## 📁 Project Structure

```
admin-telegram-bot-/
├── src/
│   ├── main/
│   │   ├── kotlin/com/aitorr/admintelegrambot/
│   │   │   ├── AdminTelegramBotApplication.kt       # Main entry point
│   │   │   ├── application/                         # Use cases
│   │   │   │   └── GetBotInfoUseCase.kt
│   │   │   ├── domain/                              # Domain models and ports
│   │   │   │   ├── model/
│   │   │   │   │   └── ChatBotUser.kt
│   │   │   │   └── port/
│   │   │   │       ├── GetChatBot.kt
│   │   │   │       └── SaveChatBotUser.kt
│   │   │   ├── infrastructure/                      # Adapters
│   │   │   │   ├── adapter/
│   │   │   │   │   └── outbound/
│   │   │   │   │       ├── ChatBotUserRepositoryJooq.kt
│   │   │   │   │       └── SaveChatBotUserAdapter.kt
│   │   │   │   ├── client/
│   │   │   │   │   └── TelegramBotClient.kt
│   │   │   │   ├── config/
│   │   │   │   │   └── TelegramBotProperties.kt
│   │   │   │   ├── jooq/                            # Generated jOOQ code
│   │   │   │   └── codegen/
│   │   │   │       └── JooqCodeGen.kt               # Code generator
│   │   │   └── resources/
│   │   │       ├── application.yml                   # Main configuration
│   │   │       └── db/migration/                     # Flyway migrations
│   │   │           └── V1__create_chat_bot_users_table.sql
│   └── test/
│       ├── kotlin/com/aitorr/admintelegrambot/
│       │   ├── application/
│       │   │   └── GetBotInfoUseCaseTest.kt         # Unit tests
│       │   ├── component/
│       │   │   └── GetBotInfoComponentTest.kt       # End-to-end tests
│       │   ├── config/
│       │   │   ├── MockTelegramBotServer.kt         # Mock server
│       │   │   └── TestcontainersConfiguration.kt
│       │   ├── infrastructure/
│       │   │   └── client/
│       │   │       └── TelegramBotClientTests.kt    # Unit tests
│       │   └── integration/
│       │       └── TelegramBotIntegrationTest.kt    # Integration tests
│       └── resources/
│           ├── application-test.yml
│           └── .testcontainers.properties
├── build.gradle                                      # Build configuration
├── gradlew                                           # Gradle wrapper (Unix)
├── gradlew.bat                                       # Gradle wrapper (Windows)
└── README.md                                         # This file
```

## 🛠️ Technology Stack

- **Language:** Kotlin 1.9.21
- **Framework:** Spring Boot 3.2.0
- **Java Version:** 21
- **Build Tool:** Gradle 8.5
- **Database:** PostgreSQL 15
- **Database Access:** jOOQ 3.18.7
- **Migrations:** Flyway 9.22.3
- **Testing:** JUnit 5, MockK, Testcontainers
- **Functional Programming:** Arrow-kt 1.2.1

## 🐛 Troubleshooting

### Docker Issues

**Problem:** Tests fail with "Could not find a valid Docker environment"

**Solution:** 
1. Start Docker Desktop
2. Verify Docker is running: `docker ps`
3. Restart your terminal/IDE

**Problem:** Port 5432 already in use

**Solution:**
```bash
# Stop any existing PostgreSQL containers
docker stop $(docker ps -q --filter ancestor=postgres:15-alpine)

# Or use a different port in application.yml
spring.datasource.url: jdbc:postgresql://localhost:5433/admintelegrambot
```

### Build Issues

**Problem:** "Cannot inline bytecode built with JVM target 11"

**Solution:** This is already fixed in the current version. Ensure you're using the latest code from this branch.

**Problem:** "Could not determine Java version"

**Solution:**
```bash
# Verify Java 21 is installed
java -version

# If using SDKMAN
sdk use java 21.0.1-tem
```

### Test Issues

**Problem:** Integration tests are slow

**Solution:** Container reuse is enabled in `.testcontainers.properties`. After the first run, subsequent runs will be faster.

**Problem:** Tests fail randomly

**Solution:** 
1. Ensure Docker has enough resources (4GB RAM recommended)
2. Clean and rebuild: `./gradlew clean build`

### jOOQ Code Generation Issues

**Problem:** "No migrations found"

**Solution:** Ensure Flyway migration files exist in `src/main/resources/db/migration/`

**Problem:** Generated code has compilation errors

**Solution:**
```bash
# Clean and regenerate
./gradlew clean
./gradlew generateJooq
./gradlew build
```

## 📚 Additional Documentation

- **jOOQ Code Generator:** See `src/main/kotlin/com/aitorr/admintelegrambot/codegen/README.md`
- **Integration Tests:** See `src/test/kotlin/com/aitorr/admintelegrambot/integration/README.md`
- **Flyway Migrations:** Located in `src/main/resources/db/migration/`

## 🔐 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/admintelegrambot` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `TELEGRAM_BOT_TOKEN` | Your Telegram bot token | (test token for development) |

## 🧹 Cleaning Up

### Remove Generated Files

```bash
./gradlew clean
```

### Remove Docker Containers

```bash
# Stop all containers
docker stop $(docker ps -aq)

# Remove all stopped containers
docker container prune -f

# Remove PostgreSQL images (optional)
docker rmi postgres:15-alpine
```

### Reset Database

If you're running PostgreSQL manually:

```bash
docker stop admin-telegram-bot-postgres
docker rm admin-telegram-bot-postgres
# Then recreate with the docker run command above
```

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review test logs in `build/test-results/`
3. Check Docker logs: `docker logs <container-id>`

## 🎯 Next Steps

1. **Run the tests** to verify everything works: `./gradlew test`
2. **Start the application** with PostgreSQL: `./gradlew bootRun`
3. **Explore the code** structure and architecture
4. **Add new features** following the hexagonal architecture pattern

---

**Happy coding! 🚀**
