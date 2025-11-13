// Docs:
// Kotlin Spring: https://kotlinlang.org/docs/jvm-get-started-spring-boot.html
package com.ride.driver.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RideDriverBackendApplication
fun main(args: Array<String>) {
        println("Ride Driver Backend Application is running at port 4000")
        runApplication<RideDriverBackendApplication>(*args)    
}
	