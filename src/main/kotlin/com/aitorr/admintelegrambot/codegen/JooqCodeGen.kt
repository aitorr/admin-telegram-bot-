package com.aitorr.admintelegrambot.codegen

import org.flywaydb.core.Flyway
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target as JooqTarget
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * jOOQ Code Generator
 * 
 * This class generates jOOQ code from the database schema using Flyway migrations.
 * 
 * Usage:
 * 1. Via Gradle: ./gradlew generateJooq
 * 2. By hand: Run this main method directly
 * 
 * The generator will:
 * 1. Start a PostgreSQL container using Testcontainers
 * 2. Run Flyway migrations to create the schema
 * 3. Generate jOOQ code from the migrated schema
 * 4. Stop the container
 */
object JooqCodeGen {

    private const val POSTGRES_VERSION = "postgres:15-alpine"
    private const val TARGET_PACKAGE = "com.aitorr.admintelegrambot.infrastructure.jooq"
    private const val TARGET_DIRECTORY = "src/main/kotlin"
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("🚀 Starting jOOQ code generation...")
        
        // Start PostgreSQL container
        val postgres = PostgreSQLContainer(DockerImageName.parse(POSTGRES_VERSION))
            .withDatabaseName("jooq_codegen")
            .withUsername("jooq")
            .withPassword("jooq")
        
        try {
            postgres.start()
            println("✅ PostgreSQL container started")
            
            // Run Flyway migrations
            val flyway = Flyway.configure()
                .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
                .locations("classpath:db/migration")
                .load()
            
            println("🔄 Running Flyway migrations...")
            val migrationsApplied = flyway.migrate()
            println("✅ Applied ${migrationsApplied.migrationsExecuted} migration(s)")
            
            // Generate jOOQ code
            println("🔄 Generating jOOQ code...")
            generateJooqCode(postgres.jdbcUrl, postgres.username, postgres.password)
            println("✅ jOOQ code generated successfully in $TARGET_DIRECTORY/$TARGET_PACKAGE")
            
        } finally {
            postgres.stop()
            println("✅ PostgreSQL container stopped")
        }
        
        println("🎉 jOOQ code generation completed!")
    }
    
    private fun generateJooqCode(jdbcUrl: String, username: String, password: String) {
        val jdbc = Jdbc()
        jdbc.driver = "org.postgresql.Driver"
        jdbc.url = jdbcUrl
        jdbc.user = username
        jdbc.password = password
        
        val database = Database()
        database.name = "org.jooq.meta.postgres.PostgresDatabase"
        database.inputSchema = "public"
        database.includes = ".*"
        database.excludes = ""
        
        val generate = Generate()
        generate.isRelations = true
        generate.isDeprecated = false
        generate.isRecords = true
        generate.isImmutablePojos = false
        generate.isFluentSetters = true
        generate.isDaos = true
        generate.isPojos = true
        
        val target = JooqTarget()
        target.packageName = TARGET_PACKAGE
        target.directory = TARGET_DIRECTORY
        
        val generator = Generator()
        generator.name = "org.jooq.codegen.KotlinGenerator"
        generator.database = database
        generator.generate = generate
        generator.target = target
        
        val configuration = Configuration()
        configuration.jdbc = jdbc
        configuration.generator = generator
        
        GenerationTool.generate(configuration)
    }
}
