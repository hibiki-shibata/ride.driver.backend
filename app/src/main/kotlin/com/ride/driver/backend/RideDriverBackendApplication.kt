// Docs:
// Kotlin Spring: https://kotlinlang.org/docs/jvm-get-started-spring-boot.html

package com.ride.driver.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.ride.driver.backend.services.CourierDataService", "com.ride.driver.backend.controller.courirRequestController"])
class RideDriverBackendApplication

fun main(args: Array<String>) {
	println("Ride Driver Backend Application is running at port 4000!")
    runApplication<RideDriverBackendApplication>(*args)
}