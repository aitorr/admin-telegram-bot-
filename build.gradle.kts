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
