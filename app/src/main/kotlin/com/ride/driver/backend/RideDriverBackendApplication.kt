// Docs:
// Kotlin Spring: https://kotlinlang.org/docs/jvm-get-started-spring-boot.html

package com.ride.driver.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
// import org.springframework.boot.runApplication

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
class RideDriverBackendApplication{
	@GetMapping("/hello")
	fun hello(@RequestParam(value = "name", defaultValue = "World") name: String): String {
		return "Hello $name!"
	}
}

fun main(args: Array<String>) {
	println("Ride Driver Backend Application is running!")
	// runApplication<RideDriverBackendApplication>(*args)
	// SpringApplication.run(RideDriverBackendApplication, args);
	SpringApplication.run(RideDriverBackendApplication::class.java, *args)
	// println("port" + System.getProperty("server.port"))
}




// @SpringBootApplication
// @RestController
// public class DemoApplication {
//     public static void main(String[] args) {
//       SpringApplication.run(DemoApplication.class, args);
//     }
//     @GetMapping("/hello")
//     public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
//       return String.format("Hello %s!", name);
//     }
// }