// Docs:
// Kotlin Spring: https://kotlinlang.org/docs/jvm-get-started-spring-boot.html
package com.ride.driver.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RideDriverBackendApplication
fun main(args: Array<String>) {
        println("Ride Driver Backend Application is running at port 300")
        runApplication<RideDriverBackendApplication>(*args)
}
	