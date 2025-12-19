# jOOQ Code Generation

This directory contains the jOOQ code generator that creates type-safe database access code from Flyway migrations.

## How It Works

1. **Starts a PostgreSQL container** using Testcontainers
2. **Runs Flyway migrations** from `src/main/resources/db/migration`
3. **Generates jOOQ code** into `src/main/kotlin/com/aitorr/admintelegrambot/infrastructure/jooq`
4. **Stops the container** automatically

## Running Code Generation

### Option 1: Via Gradle (Recommended)
```bash
./gradlew generateJooq
```

### Option 2: Run Main Method Directly
Run the `JooqCodeGen.main()` method from your IDE or:
```bash
./gradlew classes
./gradlew generateJooq
```

## Prerequisites

- **Docker** must be running
- **Java 21** (configured in build.gradle)
- **Flyway migration files** in `src/main/resources/db/migration`

## What Gets Generated

The code generator creates:

- **Tables**: Type-safe table definitions (e.g., `ChatBotUsers`)
- **Records**: Table record classes for CRUD operations
- **POJOs**: Plain Old Java/Kotlin Objects for data transfer
- **DAOs**: Data Access Objects with basic CRUD methods

All generated code goes into:
```
src/main/kotlin/com/aitorr/admintelegrambot/infrastructure/jooq/
├── DefaultCatalog.kt
├── Keys.kt
├── Public.kt
├── indexes/
├── tables/
│   ├── ChatBotUsers.kt
│   ├── pojos/
│   │   └── ChatBotUsers.kt
│   ├── records/
│   │   └── ChatBotUsersRecord.kt
│   └── daos/
│       └── ChatBotUsersDao.kt
```

## Configuration

Generation settings are in `JooqCodeGen.kt`:
- **Target Package**: `com.aitorr.admintelegrambot.infrastructure.jooq`
- **Target Directory**: `src/main/kotlin`
- **PostgreSQL Version**: `postgres:15-alpine`
- **Generator**: `KotlinGenerator` for Kotlin-friendly code

## When to Regenerate

Regenerate jOOQ code whenever you:
- ✅ Add a new Flyway migration
- ✅ Modify an existing table schema
- ✅ Add new tables
- ✅ Change column types or constraints

## Example Usage

After generation, use the generated code in your repositories:

```kotlin
@Repository
class MyRepository(private val dsl: DSLContext) {
    
    fun findUserById(id: Long): ChatBotUser? {
        return dsl.selectFrom(CHAT_BOT_USERS)
            .where(CHAT_BOT_USERS.ID.eq(id))
            .fetchOne()
            ?.into(ChatBotUser::class.java)
    }
}
```

## Troubleshooting

### Docker not running
```
Error: Could not find a valid Docker environment
```
**Solution**: Start Docker Desktop or Docker daemon

### Generation fails
1. Check that Flyway migrations are valid SQL
2. Ensure PostgreSQL container can start
3. Check logs for specific SQL errors

### Code not compiling after generation
1. Run `./gradlew clean build`
2. Refresh your IDE's Gradle project
3. Check that generated code is in the correct package

## Integration with Build

The `generateJooq` task is configured in `build.gradle` and:
- Uses Java 21 toolchain
- Depends on `processResources` (for migrations)
- Can be run independently or as part of your build process

To auto-generate on every build, add to `build.gradle`:
```gradle
tasks.named('compileKotlin') {
    dependsOn tasks.named('generateJooq')
}
```

**Note**: This is NOT enabled by default to avoid slowing down builds. Generate manually when schema changes.
