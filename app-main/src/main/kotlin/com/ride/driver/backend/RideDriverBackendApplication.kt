// Docs:
// Kotlin Spring: https://kotlinlang.org/docs/jvm-get-started-spring-boot.html
package com.ride.driver.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.beans.factory.annotation.Value

@SpringBootApplication
@EnableScheduling
class RideDriverBackendApplication(
        @Value("\${server.port}") private val serverPort: String
) {
    init {
        println("Ride Driver Backend Application is starting on port: $serverPort")
    }
}

fun main(args: Array<String>) {
        runApplication<RideDriverBackendApplication>(*args)
    }
	