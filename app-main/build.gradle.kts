plugins {
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    kotlin("plugin.jpa") version "2.2.0"
}

group = "com.ride.driver"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JPA and database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:42.7.8")

    // ✅ Flyway (Boot-managed + auto-config)
    implementation("org.springframework.boot:spring-boot-starter-flyway")
	    // override Flyway to newer (example)
    implementation("org.flywaydb:flyway-core:11.20.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.20.1")

    // ✅ Bean Validation provider (fixes NoProviderFoundException warning)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    implementation("io.jsonwebtoken:jjwt-jackson:0.13.0")
    implementation("org.mindrot:jbcrypt:0.4") // For password hashing

    // Kotlin reflection (version aligned via Gradle/Kotlin plugin)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // JSON serialization (keep if you actually use kotlinx.serialization)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-resttestclient:4.0.4")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:4.0.4")
    testImplementation("org.springframework.boot:spring-boot-restclient-test:4.0.4")
    testImplementation("io.mockk:mockk:1.13.10")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}