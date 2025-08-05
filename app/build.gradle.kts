plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id ("application")
	// id("java")
}



group = "com.ride.driver"
version = "0.0.1-SNAPSHOT"

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
	// implementation("org.springframework.data:spring-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2")

	// runtimeOnly("org.springframework.boot:spring-boot-devtools")
	// kapt("org.springframework.boot:spring-boot-configuration-processor")



	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
	implementation("jakarta.validation:jakarta.validation-api")

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

application {
	mainClass.set("com.ride.driver.backend.RideDriverBackendApplicationKt")
}




