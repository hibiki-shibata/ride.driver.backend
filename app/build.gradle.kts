plugins {
	kotlin("jvm") version "2.2.0"
	kotlin("plugin.spring") version "2.2.0"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	// id ("application")
	
	
	// kotlin("plugin.allopen") version "1.9.25"
	// kotlin("kapt") version "1.9.25"
	kotlin("plugin.jpa") version "2.2.0" // for config/dbInitializerConf.kt

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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// CORS configuration
	implementation("org.springframework:spring-webmvc")

	// JPA and database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")

	// DB migration
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.flywaydb:flyway-core") // core is included in starter-data-jpa, but explicitly adding for clarity


	// Kotlin support - used for models and services
	implementation("jakarta.validation:jakarta.validation-api")
	implementation("jakarta.persistence:jakarta.persistence-api")

	
	// For reflection, Spring need to read the Entity data class passed in the Repo args (e.g. obj:class.memberProperties)
	implementation("org.jetbrains.kotlin:kotlin-reflect") 
}


kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


// tasks.test {
//     testLogging {
//         showStandardStreams = true  // output println() values in gradle test
//     }
//     // dependsOn("startServer")
//     // finalizedBy("stopServer")
// }


// application {
// 	mainClass.set("com.ride.driver.backend.RideDriverBackendApplicationKt")
// }