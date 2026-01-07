plugins {
    kotlin("jvm") version "2.2.21"
	id("io.ktor.plugin") version "3.3.2"

}

group = "ride.driver.backend"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // implementation(libs.ktor.server.core)
    // implementation(libs.ktor.server.netty)
    // implementation(libs.logback.classic)
    // implementation(libs.ktor.server.core)
    // implementation(libs.ktor.server.config.yaml)
    // testImplementation(libs.ktor.server.test.host)
    // testImplementation(libs.kotlin.test.junit)
	implementation("io.ktor:ktor-server-core-jvm:3.3.2")
	implementation("io.ktor:ktor-server-netty-jvm:3.3.2")
	implementation("ch.qos.logback:logback-classic:1.4.14")
	implementation("io.ktor:ktor-server-config-yaml-jvm:3.3.2")
	testImplementation("io.ktor:ktor-server-test-host-jvm:3.3.2")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.2.21")
}